package com.oe.rehooked.entities.hook;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.ReHookedEntities;
import com.oe.rehooked.sound.ReHookedSounds;
import com.oe.rehooked.utils.CurioUtils;
import com.oe.rehooked.utils.HandlerHelper;
import com.oe.rehooked.utils.PositionHelper;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class HookEntity extends Projectile {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static final EntityDataAccessor<Optional<BlockPos>> HIT_POS =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> STATE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PREV_STATE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Vector3f> DELTA_MOVEMENT = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Integer> REASON = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> HOOK_TYPE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> OWNER_ID = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);

    protected int ticksInState = 0;
    protected boolean firstTickInState = true;
    protected Vec3 offset;
    
    public HookEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity pTarget) {
        return false;
    }

    public HookEntity(Player player) {
        super(ReHookedEntities.HOOK_PROJECTILE.get(), player.level());
        setNoGravity(true);
        noCulling = true;
        setOwner(player);
        setPos(player.getEyePosition());
        CurioUtils.GetHookType(player).ifPresent(this::setHookType);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return getHookData().map(hookData -> pDistance < hookData.range() * hookData.range()).orElse(false);
    }

    @Override
    public void setOwner(@Nullable Entity pOwner) {
        super.setOwner(pOwner);
        setOwnerId(pOwner != null ? pOwner.getId() : -1);
    }

    @Override
    public void tick() {
        if (!level().isClientSide() && !getState().equals(State.DONE) && 
                !(getOwner() instanceof Player owner && owner.isAlive())) {
            LOGGER.debug("Owner not found, setting state to done!");
            setState(State.DONE);
        }
        
        if (!level().isClientSide()) handleSounds();
        
        // "state machine"
        switch (getState()) {
            case SHOT -> tickShot();
            case PULLING -> tickPulling();
            case RETRACTING -> tickRetracting();
            case DONE -> {
                if (!level().isClientSide()) {
                    discard();
                }
            }
        }
        
        // update position
        Vec3 dV = getDeltaMovement();
        if (getPrevState().equals(State.SHOT)) {
            setPos(getX() + dV.x, getY() + dV.y, getZ() + dV.z);
        }
        else if (getPrevState().equals(State.RETRACTING)) {
            setPos(getX() + dV.x, getY() + dV.y, getZ() + dV.z);
        }
        
        // keep track of how many ticks in current state (also update prev state)
        trackTicksInState();

        // create offset on first tick
        if (offset == null) {
            Player owner = tryGetOwnerFromCachedId();
            if (owner != null)
                offset = position().vectorTo(owner.position().add(0, owner.getEyeHeight() - 0.1, 0)).normalize();
        }
        
        // run the super class tick method
        super.tick();
    }
    
    protected void handleSounds() {
        boolean handledReason = false;
        switch (getReason()) {
            case SHOT -> {
                level().playSound(null, getX(), getY(), getZ(), ReHookedSounds.HOOK_SHOOT.get(), SoundSource.PLAYERS, 0.2f, 0.5f);
                handledReason = true;
            }
            case HIT -> {
                getHitPos().ifPresent(hitPos -> {
                    SoundType soundType = level().getBlockState(hitPos).getSoundType();
                    level().playSound(null, hitPos.getX(), hitPos.getY(), hitPos.getZ(),
                            soundType.getHitSound(),
                            SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch());
                    level().playSound(null, hitPos.getX(), hitPos.getY(), hitPos.getZ(),
                            ReHookedSounds.HOOK_HIT.get(), 
                            SoundSource.BLOCKS, 0.35f, 0.5f);
                });
                handledReason = true;
            }
            case MISS, BREAK -> {
                if (getOwner() instanceof Player owner)
                    level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), ReHookedSounds.HOOK_MISS.get(), SoundSource.PLAYERS, 0.5f, 1f);
                handledReason = true;
            }
            case PLAYER -> {
                level().playSound(null, getX(), getY(), getZ(), ReHookedSounds.HOOK_RETRACT.get(), SoundSource.NEUTRAL, 1f, 1f);
                handledReason = true;
            }
        }
        if (handledReason) setReason(Reason.EMPTY);
    }
    
    @OnlyIn(Dist.CLIENT)
    public void createParticles() {
        getHookData().map(HookData::particleType).map(Supplier::get).ifPresent(particleType -> {
            Player owner = tryGetOwnerFromCachedId();
            if (owner != null) {
                Vec3 ownerWaist = PositionHelper.getWaistPosition(owner);
                Vec3 particleSpeed = owner.getDeltaMovement().reverse().scale(0.8);
                double rDist = ownerWaist.distanceTo(position()) + 1;
                for (int i = 0; i < rDist; i++) {
                    for (int k = 0; k < 2; k++) {
                        double lerpX = Mth.lerp((double) (i + 0.5f * k) / rDist, ownerWaist.x, getX());
                        double lerpY = Mth.lerp((double) (i + 0.5f * k) / rDist, ownerWaist.y, getY());
                        double lerpZ = Mth.lerp((double) (i + 0.5f * k) / rDist, ownerWaist.z, getZ());
                        level().addParticle(particleType,
                                lerpX + Math.random() * 0.25,
                                lerpY + Math.random() * 0.15,
                                lerpZ + Math.random() * 0.25,
                                particleSpeed.x, particleSpeed.y, particleSpeed.z);
                    }
                }
            }
        });
    }

    @Override
    public void setDeltaMovement(Vec3 pDeltaMovement) {
        setSharedDeltaV(pDeltaMovement.toVector3f());
        super.setDeltaMovement(pDeltaMovement);
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return new Vec3(getSharedDeltaV());
    }

    @Override
    public void shootFromRotation(@NotNull Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        LOGGER.debug("Shooting!");
        float f = -Mth.sin(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((pX + pZ) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, pVelocity, pInaccuracy);
        setReason(Reason.SHOT);
    }

    protected void tickShot() {
        // move the hook in the goal direction, checking for hits in the process
        Optional<HookData> optHookData = getHookData();
        if (optHookData.isPresent()) {
            HookData hookData = optHookData.get();
            // check if needs to destroy instant hook
            if (!level().isClientSide() && !firstTickInState && hookData.speed() / 20f >= hookData.range()) {
                LOGGER.debug("Retracting instant hook on second shot tick");
                setReason(Reason.MISS);
                setState(State.RETRACTING);
                setDeltaMovement(Vec3.ZERO);
                return;
            }
            Player owner = tryGetOwnerFromCachedId();
            if (owner != null) {
                // check if moved further than the target
                if (PositionHelper.getWaistPosition(owner).distanceTo(position()) > hookData.range()) {
                    LOGGER.debug("Moved further than range from owner");
                    setReason(Reason.MISS);
                    setState(State.RETRACTING);
                    setDeltaMovement(Vec3.ZERO);
                }
                else {
                    // check if hit anything
                    BlockHitResult hitResult = VectorHelper.getFromEntityAndAngle(this, this.getDeltaMovement().normalize(), this.getDeltaMovement().length());
                    BlockState hitState = level().getBlockState(hitResult.getBlockPos());
                    if (!hitState.isAir() && hitResult.getType().equals(HitResult.Type.BLOCK)) {
                        LOGGER.debug("Hit a block at {}", hitResult.getBlockPos().getCenter());
                        Vec3 hitLocation = hitResult.getLocation();
                        setDeltaMovement(position().vectorTo(hitLocation));
                        if (!level().isClientSide()) {
                            setReason(Reason.HIT);
                            setState(State.PULLING);
                            setHitPos(hitResult.getBlockPos());
                        }
                    }
                }
            }
        }
    }
    
    protected void tickPulling() {
        if (firstTickInState) {
            // stop moving
            setDeltaMovement(Vec3.ZERO);
        }
        getHitPos().ifPresent(hitPos -> {
            if (level().getBlockState(hitPos).isAir()) {
                setReason(Reason.BREAK);
                setState(State.RETRACTING);
            }
        });
        // block collision detection while pulling to prevent rendering as black blob
        Player owner = tryGetOwnerFromCachedId();
        if (isInWall() && owner != null) {
            setPos(position().add(position().vectorTo(owner.position()).normalize().scale(0.1)));
        }
    }
    
    protected void tickRetracting() {
        if (!level().isClientSide()) {
            if (getOwner() instanceof Player owner) {
                // vector to the owner
                Vec3 vectorToPlayer = position().vectorTo(PositionHelper.getWaistPosition(owner));
                if (vectorToPlayer.length() < 5) {
                    HandlerHelper.getHookHandler(owner).ifPresent(handler -> {
                        handler.removeHook(this);
                        handler.killHook(getId());
                    });
                }
                // set the delta movement according to speed and distance from player
                getHookData().ifPresent(hookData -> {
                    float speedModifier = hookData.speed() == Float.MAX_VALUE ? hookData.range() : hookData.speed() / 10f;
                    speedModifier += (float) owner.getDeltaMovement().length();
                    if (vectorToPlayer.length() > speedModifier) {
                        setDeltaMovement(vectorToPlayer.normalize().scale(speedModifier));
                    } else {
                        setDeltaMovement(vectorToPlayer);
                    }
                });
            }
        }
    }
    
    protected void trackTicksInState() {
        State currState = getState();
        if (currState.equals(getPrevState())) {
            ticksInState++;
            firstTickInState = false;
        }
        else {
            firstTickInState = true;
            ticksInState = 0;
            LOGGER.debug("Hook changed from {} to {}", getPrevState(), currState);
            setPrevState(currState);
        }
    }

    public boolean hasChain() {
        return getHookData()
                .map(hookData -> hookData.particleType().get() == null)
                .orElse(false);
    }
    
    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }
    
    public void setState(State state) {
        entityData.set(STATE, state.ordinal());
    }
    
    public State getState() {
        return State.values()[entityData.get(STATE)];
    }
    
    protected void setHitPos(BlockPos hitPos) {
        entityData.set(HIT_POS, Optional.ofNullable(hitPos));
    }
    
    public Optional<BlockPos> getHitPos() {
        return entityData.get(HIT_POS);
    }
    
    public Optional<HookData> getHookData() {
        return HookRegistry.getHookData(getHookType());
    }
    
    public String getHookType() {
        return entityData.get(HOOK_TYPE);
    }
    
    public void setHookType(String hookType) {
        entityData.set(HOOK_TYPE, hookType);
    }
    
    protected void setPrevState(State state) {
        entityData.set(PREV_STATE, state.ordinal());
    }
    
    public State getPrevState() {
        return State.values()[entityData.get(PREV_STATE)];
    }
    
    protected void setSharedDeltaV(Vector3f deltaV) {
        entityData.set(DELTA_MOVEMENT, deltaV);
    }
    
    public Vector3f getSharedDeltaV() {
        return entityData.get(DELTA_MOVEMENT);
    }
    
    public void setReason(Reason reason) {
        entityData.set(REASON, reason.ordinal());
    }
    
    public Reason getReason() {
        return Reason.values()[entityData.get(REASON)];
    }
    
    public Integer getOwnerId() {
        return entityData.get(OWNER_ID);
    }
    
    public void setOwnerId(Integer id) {
        entityData.set(OWNER_ID, id);
    }
    
    public Player tryGetOwnerFromCachedId() {
        if (level().getEntity(getOwnerId()) instanceof Player owner)
            return owner;
        return null;
    }
    
    @Override
    protected void defineSynchedData() {
        entityData.define(HIT_POS, Optional.empty());
        entityData.define(STATE, State.SHOT.ordinal());
        entityData.define(PREV_STATE, State.SHOT.ordinal());
        entityData.define(DELTA_MOVEMENT, new Vector3f(0, 0, 0));
        entityData.define(REASON, Reason.EMPTY.ordinal());
        entityData.define(HOOK_TYPE, "");
        entityData.define(OWNER_ID, -1);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    public enum State {
        SHOT,
        PULLING,
        RETRACTING,
        DONE
    }
    
    public enum Reason {
        EMPTY,
        SHOT,
        HIT,
        MISS,
        PLAYER,
        BREAK
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        LOGGER.debug("Hook {} getting removed for {}", getUUID(), pReason);
        super.remove(pReason);
    }
}
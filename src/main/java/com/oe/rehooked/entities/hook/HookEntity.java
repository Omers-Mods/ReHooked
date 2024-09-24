package com.oe.rehooked.entities.hook;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.ReHookedEntities;
import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.sound.ReHookedSounds;
import com.oe.rehooked.utils.CurioUtils;
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
import net.minecraft.world.item.ItemStack;
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
    protected boolean canHitEntity(Entity pTarget) {
        return false;
    }

    public HookEntity(Player player) {
        super(ReHookedEntities.HOOK_PROJECTILE.get(), player.level());
        setNoGravity(true);
        noCulling = true;
        setOwner(player);
        setPos(player.getEyePosition());
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
        if (offset == null && getOwner() instanceof Player owner) {
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
                    level().playSound(null, getOwner().getX(), owner.getY(), owner.getZ(), ReHookedSounds.HOOK_MISS.get(), SoundSource.PLAYERS, 0.5f, 1f);
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
        getHookType().flatMap(HookRegistry::getHookData).map(HookData::particleType).map(Supplier::get).ifPresent(particleType -> {
            if (getOwner() instanceof Player owner) {
                Vec3 ownerWaist = PositionHelper.getWaistPosition(owner);
                Vec3 toOwner = position().vectorTo(ownerWaist).normalize().scale(0.2);
                for (int i = 1; i < 26; i++) {
                    level().addParticle(particleType,
                            Mth.lerp((double) i / 50d, ownerWaist.x, getX()),
                            Mth.lerp((double) i / 50d, ownerWaist.y, getY()),
                            Mth.lerp((double) i / 50d, ownerWaist.z, getZ()),
                            toOwner.x, toOwner.y, toOwner.z);
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
    public Vec3 getDeltaMovement() {
        return new Vec3(getSharedDeltaV());
    }

    @Override
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        LOGGER.debug("Shooting!");
        float f = -Mth.sin(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((pX + pZ) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, pVelocity, pInaccuracy);
        setReason(Reason.SHOT);
    }

    protected void tickShot() {
        // move the hook in the goal direction, checking for hits in the process
        Optional<HookData> optHookData = getHookType().flatMap(HookRegistry::getHookData);
        if (optHookData.isPresent()) {
            HookData hookData = optHookData.get();
            // check if needs to destroy instant hook
            if (!level().isClientSide() && !firstTickInState && hookData.speed() == Float.MAX_VALUE) {
                LOGGER.debug("Retracting instant hook on second shot tick");
                setReason(Reason.MISS);
                setState(State.RETRACTING);
                setDeltaMovement(Vec3.ZERO);
                return;
            }
            if (getOwner() instanceof Player owner) {
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
            if (level().isClientSide()){
                // move client pos to hit
                getHitPos().map(BlockPos::getCenter).ifPresent(pos -> {
                    if (offset != null) setPos(pos.add(offset.scale(0.5)));
                    else setPos(pos);
                });
            }
        }
        getHitPos().ifPresent(hitPos -> {
            if (level().getBlockState(hitPos).isAir()) {
                setReason(Reason.BREAK);
                setState(State.RETRACTING);
            }
        });
        // block collision detection while pulling to prevent rendering as black blob
        if (isInWall() && getOwner() instanceof Player owner) {
            setPos(position().add(position().vectorTo(owner.position()).normalize().scale(0.1)));
        }
    }
    
    protected void tickRetracting() {
        if (getOwner() instanceof Player owner) {
            if (level().isClientSide()) {
                // vector to the owner
                Vec3 vectorToPlayer = position().vectorTo(PositionHelper.getWaistPosition(owner));
                if (vectorToPlayer.length() < 5) {
                    IClientPlayerHookHandler.FromPlayer(owner).ifPresent(handler -> {
                        handler.removeHook(this);
                        setState(State.DONE);
                        handler.killHook(getId());
                    });
                }
                // set the delta movement according to speed and distance from player
                getHookType().flatMap(HookRegistry::getHookData).ifPresent(hookData -> {
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
        return getHookType()
                .flatMap(HookRegistry::getHookData)
                .map(hookData -> !(hookData.particleType() != null && hookData.isCreative()))
                .orElse(false);
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
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
    
    public Optional<String> getHookType() {
        return Optional.ofNullable(getOwner()).flatMap(owner -> CurioUtils.GetCuriosOfType(HookItem.class, (Player) owner))
                .flatMap(CurioUtils::GetIfUnique)
                .map(ItemStack::getItem)
                .map(item -> (HookItem) item)
                .map(HookItem::getHookType);
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
    
    @Override
    protected void defineSynchedData() {
        entityData.define(HIT_POS, Optional.empty());
        entityData.define(STATE, State.SHOT.ordinal());
        entityData.define(PREV_STATE, State.SHOT.ordinal());
        entityData.define(DELTA_MOVEMENT, new Vector3f(0, 0, 0));
        entityData.define(REASON, Reason.EMPTY.ordinal());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
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
    public void remove(RemovalReason pReason) {
        LOGGER.debug("Hook {} getting removed for {}", getUUID(), pReason);
        super.remove(pReason);
    }
}
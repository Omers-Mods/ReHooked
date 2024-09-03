package com.oe.rehooked.entities.hook;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.ReHookedEntities;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.utils.CurioUtils;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.Optional;

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
        setOwner(player);
        Vec3 adjusted = new Vec3(player.getX(), player.getY() + player.getEyeHeight() - 0.1, player.getZ());
        setPos(adjusted);
    }
    
    @Override
    public void tick() {
        if (!getState().equals(State.RETRACTING) && !(getOwner() instanceof Player)) {
            if (!level().isClientSide()) {
                LOGGER.debug("Owner not found, retracting");
                setState(State.RETRACTING);
            }
        }
        
        // "state machine"
        switch (getState()) {
            case SHOT -> tickShot();
            case PULLING -> tickPulling();
            case RETRACTING -> tickRetracting();
        }
        
        // update position
        if (getPrevState().equals(State.SHOT) || getPrevState().equals(State.RETRACTING)) {
            Vec3 dV = getDeltaMovement();
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
        super.shootFromRotation(pShooter, pX, pY, pZ, pVelocity, pInaccuracy);
    }

    protected void tickShot() {
        // move the hook in the goal direction, checking for hits in the process
        Optional<HookData> optHookData = getHookType().flatMap(HookRegistry::getHookData);
        if (optHookData.isPresent()) {
            HookData hookData = optHookData.get();
            // check if needs to destroy instant hook
            if (!level().isClientSide() && !firstTickInState && hookData.speed() == Float.MAX_VALUE) {
                LOGGER.debug("Retracting instant hook on second shot tick");
                setState(State.RETRACTING);
                setDeltaMovement(Vec3.ZERO);
                return;
            }
            // check if moved further than the target
            if (!level().isClientSide() &&
                    getOwner() instanceof Player owner &&
                    owner.position().add(0, owner.getEyeHeight() - 0.1, 0).distanceTo(position()) > hookData.range()) {
                LOGGER.debug("Moved further than range from owner");
                setState(State.RETRACTING);
            }
            else {
                // check if hit anything
                BlockHitResult hitResult = VectorHelper.getFromEntityAndAngle(this, this.getDeltaMovement(), this.getDeltaMovement().length() + 2.5);
                BlockState hitState = level().getBlockState(hitResult.getBlockPos());
                if (!hitState.isAir() && hitResult.getType().equals(HitResult.Type.BLOCK)) {
                    LOGGER.debug("Hit a block at {}", hitResult.getBlockPos().getCenter());
                    setDeltaMovement(position().vectorTo(hitResult.getLocation()));
                    if (!level().isClientSide()) {
                        setState(State.PULLING);
                        setHitPos(hitResult.getBlockPos());
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
            if (level().getBlockState(hitPos).isAir())
                setState(State.RETRACTING);
        });
    }
    
    protected void tickRetracting() {
        if (getOwner() instanceof Player owner) {
            // vector to the owner
            Vec3 vectorToPlayer = position().vectorTo(owner.getEyePosition());
            // set the delta movement according to speed and distance from player
            getHookType().flatMap(HookRegistry::getHookData).map(HookData::speed).ifPresent(speed -> {
                if (vectorToPlayer.length() > speed / 10f) {
                    setDeltaMovement(vectorToPlayer.normalize().scale(speed / 10f).add(owner.getDeltaMovement()));
                } else {
                    setDeltaMovement(vectorToPlayer.add(owner.getDeltaMovement()));
                }
            });
            if (firstTickInState) {
                // send update to handler
                if (!level().isClientSide()) {
                    IServerPlayerHookHandler.FromPlayer(owner).ifPresent(handler -> handler.removeHook(this));
                }
            }
            if (vectorToPlayer.length() < 5) discard();
        } else if (!level().isClientSide()){
            // if owner not found discard
            discard();
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
    
    @Override
    protected void defineSynchedData() {
        entityData.define(HIT_POS, Optional.empty());
        entityData.define(STATE, State.SHOT.ordinal());
        entityData.define(PREV_STATE, State.SHOT.ordinal());
        entityData.define(DELTA_MOVEMENT, new Vector3f(0, 0, 0));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    public enum State {
        SHOT,
        PULLING,
        RETRACTING,
    }
}
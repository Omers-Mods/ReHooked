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
import net.minecraftforge.network.NetworkHooks;
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

    protected int ticksInState = 0;
    protected boolean firstTickInState = true;
    
    public HookEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    
    public HookEntity(Player player) {
        super(ReHookedEntities.HOOK_PROJECTILE.get(), player.level());
        setNoGravity(true);
        setOwner(player);
        setPos(player.getX(), player.getEyeY() - 0.25, player.getZ());
    }
    
    @Override
    public void tick() {
        if (!(getOwner() instanceof Player)) {
            if (!level().isClientSide()) {
                LOGGER.debug("Owner not found, retracting");
                setState(State.RETRACTING);
                return;
            }
        }

        // run the super class tick method
        super.tick();
        
        // "state machine"
        switch (getState()) {
            case SHOT -> tickShot();
            case PULLING -> tickPulling();
            case RETRACTING -> tickRetracting();
        }
        // update position
        if (getState().equals(State.SHOT)) {
            Vec3 dV = getDeltaMovement();
            LOGGER.debug("Moved from {}", position());
            setPos(getX() + dV.x, getY() + dV.y, getZ() + dV.z);
            LOGGER.debug("Moved to {}", position());
        }
        // keep track of how many ticks in current state (also update prev state)
        trackTicksInState();
    }

    @Override
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        LOGGER.debug("Shooting!");
        super.shootFromRotation(pShooter, pX, pY, pZ, pVelocity, pInaccuracy);
        setDeltaMovement(getDeltaMovement());
    }

    protected void tickShot() {
        if (level().isClientSide()) return;
        // move the hook in the goal direction, checking for hits in the process
        Optional<HookData> optHookData = getHookType().flatMap(HookRegistry::getHookData);
        if (optHookData.isPresent()) {
            HookData hookData = optHookData.get();
            // check if hit anything
            BlockHitResult hitResult = VectorHelper.getFromEntityAndAngle(this, this.getDeltaMovement(), this.getDeltaMovement().length());
            BlockState hitState = level().getBlockState(hitResult.getBlockPos());
            if (!hitState.isAir() && hitResult.getType().equals(HitResult.Type.BLOCK)) {
                LOGGER.debug("Hit a block at {}", hitResult.getBlockPos().getCenter());
                setState(State.PULLING);
                Vec3 hitCenter = hitResult.getBlockPos().getCenter();
                setDeltaMovement(position().vectorTo(hitCenter)
                        .subtract(hitCenter.vectorTo(position()).normalize().scale(0.75)));
                setHitPos(BlockPos.containing(position().add(getDeltaMovement())));
            }
            else {
                 setDeltaMovement(getDeltaMovement());
            }
            // check if moved further than the target
            if (getOwner().position().distanceTo(position()) > hookData.range()) {
                LOGGER.debug("Moved further than range from owner");
                setState(State.RETRACTING);
            }
        }
    }
    
    protected void tickPulling() {
        if (firstTickInState) {
            // stop moving
            setDeltaMovement(Vec3.ZERO);
        }
        getHitPos().ifPresent(hitPos -> {
            if (level().getBlockState(hitPos).isAir())
                setState(State.RETRACTING);
        });
    }
    
    protected void tickRetracting() {
        // send update to handler
        if (!level().isClientSide()) {
            if (getOwner() instanceof Player owner) {
                IServerPlayerHookHandler.FromPlayer(owner).ifPresent(handler -> handler.removeHook(this));
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
    
    @Override
    protected void defineSynchedData() {
        entityData.define(HIT_POS, Optional.empty());
        entityData.define(STATE, State.SHOT.ordinal());
        entityData.define(PREV_STATE, State.SHOT.ordinal());
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
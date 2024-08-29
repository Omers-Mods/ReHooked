package com.oe.rehooked.entities.hook;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.capabilities.hooks.IPlayerHookHandler;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.ReHookedEntities;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class HookEntity extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static final EntityDataAccessor<Integer> TICKS_TO_TRAVEL = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> TYPE =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<BlockPos> GOAL_BLOCK = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Optional<BlockState>> HIT_STATE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Optional<BlockPos>> HIT_POS =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> STATE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> PREV_STATE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);

    protected int ticksInState;
    protected boolean firstTickInState;
    
    public HookEntity(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HookEntity(Player player, String hookType) {
        super(ReHookedEntities.HOOK_PROJECTILE.get(), player.level());
        setNoGravity(true);
        setOwner(player);
        setHookType(hookType);
        setState(State.IDLE.ordinal());
        setPos(player.getEyePosition());
        // set goal
        HookRegistry.getHookData(getHookType()).ifPresent(hookData -> {
            setGoalBlock(VectorHelper.getLookingAt(player, hookData.range()).getBlockPos());
        });
    }
    
    public void onInit() {
        ticksInState = 0;
        this.lookAt(EntityAnchorArgument.Anchor.EYES, getGoalBlock().getCenter());
        this.setState(State.SHOT.ordinal());
    }
    
    @Override
    public void tick() {
        switch (State.values()[getState()]) {
            case SHOT -> tickShot();
            case PULLING -> tickPulling();
            case RETRACTING -> tickRetracting();
            case IDLE -> tickIdle();
        }
        trackTicksInState();
        super.tick();
    }
    
    protected void tickShot() {
        // move the hook in the goal direction, checking for hits in the process
        HookRegistry.getHookData(getHookType()).ifPresent(hookData -> {
            // check if there is a collision in the next immediate move
            Vec3 lookAngle = position().vectorTo(getGoalBlock().getCenter()).normalize();
            double range = hookData.speed() == Double.MAX_VALUE ? hookData.range() : hookData.speed() / 20.0;
            BlockHitResult hitResult = VectorHelper.getFromEntityAndAngle(this, lookAngle, range);
            BlockState hitState = level().getBlockState(hitResult.getBlockPos());
            Vec3 moveAmount = Vec3.ZERO;
            if (!hitState.isAir() && hitState.getFluidState().equals(Fluids.EMPTY.defaultFluidState())) {
                // there is a collision with solid block
                moveAmount = hitResult.getBlockPos().getCenter().add(lookAngle.scale(-0.5)).subtract(position());
                setState(State.PULLING.ordinal());
                setHitPos(hitResult.getBlockPos());
                setHitState(hitState);
            }
            else {
                // no collision with solid block
                moveAmount = lookAngle.scale(hookData.speed() / 20.0);
                move(MoverType.SELF, lookAngle.scale(hookData.speed() / 20.0));
            }
            move(MoverType.SELF, moveAmount);
        });
    }
    
    protected void tickPulling() {
        if (firstTickInState) {
            // send update to handler
            getOwner().ifPresent(owner -> IPlayerHookHandler.FromPlayer(owner).ifPresent(IPlayerHookHandler::update));
        }
        HookRegistry.getHookData(getHookType()).ifPresent(hookData -> {
            Player player = null;
            if (level().isClientSide()) {
                // client side movement
                player = Minecraft.getInstance().player;
            } else {
                // server side movement
                player = getOwner().orElse(null);
            }
            if (player != null && player.position().distanceTo(position()) > 0.2) {
                Vec3 deltaV = player
                        .position()
                        .vectorTo(position())
                        .normalize()
                        .scale(hookData.pullSpeed() / 20.0);
                player.addDeltaMovement(deltaV);
            }
            // todo: add a check for hit block breaking -> retract hook
        });
    }
    
    protected void tickIdle() {
        // run onInit on both client and server
        onInit();
        // discard after 20 ticks
        if (ticksInState >= 20) discard();
        else ticksInState++;
    }
    
    protected void tickRetracting() {
        if (firstTickInState) {
            // send update to handler
            getOwner().ifPresent(owner -> IPlayerHookHandler.FromPlayer(owner).ifPresent(IPlayerHookHandler::update));
        }
        // todo: retract to player before discarding maybe
        discard();
    }
    
    protected void trackTicksInState() {
        int currState = getState();
        if (currState == getPrevState()) {
            ticksInState++;
            firstTickInState = false;
        }
        else {
            firstTickInState = true;
            ticksInState = 0;
            LOGGER.debug("Hook changed from {} to {}", State.values()[getPrevState()], State.values()[currState]);
            setPrevState(currState);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
    }
    
    protected void setOwner(Player player) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(player.getUUID()));
    }
    
    public UUID getOwnerUUID() {
        return entityData.get(OWNER_UUID).orElse(null);
    }
    
    public Optional<Player> getOwner() {
        return entityData.get(OWNER_UUID).flatMap(uuid -> {
            if (level() instanceof ServerLevel)
                return Optional.of((Player) ((ServerLevel) level()).getEntity(uuid));
            else 
                return Optional.empty();
        });
    }
    
    public void setState(int state) {
        entityData.set(STATE, state);
    }
    
    public int getState() {
        return entityData.get(STATE);
    }
    
    protected void setHitState(BlockState hitState) {
        entityData.set(HIT_STATE, Optional.ofNullable(hitState));
    }
    
    public Optional<BlockState> getHitState() {
        return entityData.get(HIT_STATE);
    }
    
    protected void setHitPos(BlockPos hitPos) {
        entityData.set(HIT_POS, Optional.ofNullable(hitPos));
    }
    
    public Optional<BlockPos> getHitPos() {
        return entityData.get(HIT_POS);
    }
    
    protected void setHookType(String hookType) {
        entityData.set(TYPE, hookType);
    }
    
    public String getHookType() {
        return entityData.get(TYPE);
    }
    
    protected void setTicksToTravel(int ticksToTravel) {
        entityData.set(TICKS_TO_TRAVEL, ticksToTravel);
    }
    
    public int getTicksToTravel() {
        return entityData.get(TICKS_TO_TRAVEL);
    }
    
    protected void setGoalBlock(BlockPos goalBlock) {
        entityData.set(GOAL_BLOCK, goalBlock);
    }
    
    public BlockPos getGoalBlock() {
        return entityData.get(GOAL_BLOCK);
    }
    
    protected void setPrevState(int state) {
        entityData.set(PREV_STATE, state);
    }
    
    public int getPrevState() {
        return entityData.get(PREV_STATE);
    }
    
    @Override
    protected void defineSynchedData() {
        entityData.define(GOAL_BLOCK, blockPosition());
        entityData.define(TICKS_TO_TRAVEL, 20);
        entityData.define(TYPE, "");
        entityData.define(HIT_POS, Optional.empty());
        entityData.define(HIT_STATE, Optional.empty());
        entityData.define(STATE, State.IDLE.ordinal());
        entityData.define(PREV_STATE, State.IDLE.ordinal());
        entityData.define(OWNER_UUID, Optional.empty());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    public enum State {
        SHOT,
        PULLING,
        RETRACTING,
        IDLE
    }
}

package com.oe.rehooked.handlers.hook.server;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.client.CHookCapabilityPacket;
import com.oe.rehooked.utils.PositionHelper;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SPlayerHookHandler implements IServerPlayerHookHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private final List<HookEntity> hooks;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Player> owner;
    
    private Vec3 moveVector;
    private Vec3 momentum;
    private Vec3 lastPlayerPosition;
    
    private FlightHandler flightHandler;
    
    public SPlayerHookHandler() {
        hooks = new ArrayList<>();
        owner = Optional.empty();
        moveVector = null;
        flightHandler = new FlightHandler();
    }

    @Override
    public void addHook(int id) {
        LOGGER.debug("Adding new hook by id: {}", id);
        // hooks will always be added on the server first
        getOwner().map(Player::level).map(level -> level.getEntity(id)).map(entity -> {
            if (entity instanceof HookEntity hookEntity) {
                hooks.add(hookEntity);
                return hookEntity;
            }
            return null;
        }).flatMap(hookEntity -> getOwner()).ifPresent(owner -> PacketHandler.sendToPlayer(
                new CHookCapabilityPacket(CHookCapabilityPacket.State.ADD_HOOK, id),
                (ServerPlayer) owner));
    }

    @Override
    public void addHook(HookEntity hookEntity) {
        LOGGER.debug("Adding new hook by entity with id: {}", hookEntity.getId());
        // hooks will always be added on the server first
        hooks.add(hookEntity);
        getOwner().ifPresent(owner -> PacketHandler.sendToPlayer(
                new CHookCapabilityPacket(CHookCapabilityPacket.State.ADD_HOOK, hookEntity.getId()), 
                (ServerPlayer) owner));
    }

    @Override
    public void removeHook(int id) {
        LOGGER.debug("Removing hook by id: {}", id);
        // this is a response to a request from the client
        if (hooks.removeIf(hookEntity -> hookEntity.getId() == id)) {
            // update the hook to retract
            getOwner().ifPresent(owner -> {
                if (owner.level().getEntity(id) instanceof HookEntity hookEntity) {
                    hookEntity.setReason(HookEntity.Reason.PLAYER);
                    hookEntity.setState(HookEntity.State.RETRACTING);
                }
            });
        }
    }

    @Override
    public void removeHook(HookEntity hookEntity) {
        LOGGER.debug("Removing hook by entity with id: {}", hookEntity.getId());
        // this is a response to a request from the hook
        if (hooks.remove(hookEntity)) {
            // notify client player
            getOwner().ifPresent(owner -> PacketHandler.sendToPlayer(
                    new CHookCapabilityPacket(CHookCapabilityPacket.State.RETRACT_HOOK, hookEntity.getId()), 
                    (ServerPlayer) owner));
            if (hookEntity.getState().equals(HookEntity.State.RETRACTING)) {
                hookEntity.setReason(HookEntity.Reason.EMPTY);
                hookEntity.setState(HookEntity.State.DONE);
            }
            else {
                hookEntity.setReason(HookEntity.Reason.PLAYER);
                hookEntity.setState(HookEntity.State.RETRACTING);
            }
        }
    }

    @Override
    public void removeAllHooks() {
        LOGGER.debug("Removing all hooks ({})", hooks.size());
        // this is a response to a request from the client
        hooks.forEach(hookEntity -> {
            hookEntity.setReason(HookEntity.Reason.PLAYER);
            hookEntity.setState(HookEntity.State.RETRACTING);
        });
        hooks.clear();
    }

    @Override
    public void shootFromRotation(float xRot, float yRot) {
        LOGGER.debug("Shooting from rotation: {}, {}", xRot, yRot);
        // this is a response to a client request
        getOwner().ifPresent(owner -> getHookData().ifPresent(hookData -> {
            if (hooks.size() + 1 > hookData.count())
                removeHook(hooks.get(0));
            HookEntity hookEntity = new HookEntity(owner);
            owner.level().addFreshEntity(hookEntity);
            addHook(hookEntity);
            hookEntity.shootFromRotation(owner, xRot, yRot, 0,
                    Math.min(hookData.speed() / 20f, hookData.range()), 0);
        }));
    }

    @Override
    public ICommonPlayerHookHandler setOwner(Player owner) {
        this.owner = Optional.of(owner);
        return this;
    }

    @Override
    public Optional<Player> getOwner() {
        return owner;
    }
    
    @Override
    public void update() {
        moveVector = null;
        getOwner().ifPresent(owner -> {
            flightHandler.updateFlight((ServerPlayer) owner, this);
            getHookData().ifPresent(hookData -> {
                if (countPulling() == 0) return;
                owner.resetFallDistance();
                owner.setOnGround(false);
                
                float vPT = hookData.pullSpeed() / 20f;
                Vec3 ownerWaistPos = PositionHelper.getWaistPosition(owner);
                if (!hookData.isCreative()) {
                    Vec3 pullCenter = getPullCenter();
                    double x = pullCenter.x - ownerWaistPos.x;
                    double y = pullCenter.y - ownerWaistPos.y;
                    double z = pullCenter.z - ownerWaistPos.z;
                    moveVector = new Vec3(x, y, z);
                }
                else {
                    // if player going out of the box put him back in
                    VectorHelper.Box box = getBox();
                    if (!box.isInside(ownerWaistPos)) {
                        moveVector = ownerWaistPos.vectorTo(box.closestPointInCube(ownerWaistPos));
                    }
                    else {
                        return;
                    }
                }
                // check if player is stuck against collider in a certain direction -> shouldn't pull, it causes glitches
                moveVector = reduceCollisions(moveVector);
                if (moveVector.length() > vPT) moveVector = moveVector.normalize().scale(vPT);
                if (moveVector.length() < THRESHOLD) moveVector = Vec3.ZERO;
            });
            updateMomentum();
        });
    }

    @Override
    public boolean shouldMoveThisTick() {
        return moveVector != null;
    }

    @Override
    public Vec3 getDeltaVThisTick() {
        return moveVector;
    }

    @Override
    public Collection<HookEntity> getHooks() {
        return hooks;
    }

    @Override
    public void setDeltaVThisTick(Vec3 dV) {
        moveVector = dV;
    }

    @Override
    public Vec3 getMomentum() {
        return momentum;
    }

    @Override
    public void setMomentum(Vec3 momentum) {
        this.momentum = momentum;
    }

    @Override
    public Optional<Vec3> getLastPlayerPosition() {
        return Optional.ofNullable(lastPlayerPosition);
    }

    @Override
    public void storeLastPlayerPosition() {
        getOwner().ifPresent(owner -> lastPlayerPosition = owner.position());
    }

    @Override
    public void removeAllClientHooks(ServerPlayer player) {
        PacketHandler.sendToPlayer(new CHookCapabilityPacket(CHookCapabilityPacket.State.RETRACT_ALL_HOOKS), player);
    }

    @Override
    public void copyFrom(IServerPlayerHookHandler handler) {
        if (handler instanceof SPlayerHookHandler other) {
            this.flightHandler = other.flightHandler;
        }
    }
}

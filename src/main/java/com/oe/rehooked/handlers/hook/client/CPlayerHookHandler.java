package com.oe.rehooked.handlers.hook.client;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.server.SHookCapabilityPacket;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CPlayerHookHandler implements IClientPlayerHookHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private final List<HookEntity> hooks;
    private Optional<Player> owner;
    
    private Vec3 moveVector;
    
    public CPlayerHookHandler() {
        hooks = new ArrayList<>();
        owner = Optional.empty();
        moveVector = null;
    }
    
    @Override
    public void addHook(int id) {
        LOGGER.debug("Adding hook with id: {}", id);
        if (owner.isPresent()) {
            Player player = owner.get();
            Entity entity = player.level().getEntity(id);
            if (entity instanceof HookEntity hookEntity) {
                LOGGER.debug("Hook entity is being added!");
                hooks.add(hookEntity);
                hookEntity.setOwner(player);
            }
        } else {
            LOGGER.debug("Owner not found!");
        }
    }

    @Override
    public void addHook(HookEntity hookEntity) {
        owner.ifPresentOrElse(owner -> {
            hooks.add(hookEntity);
            hookEntity.setOwner(owner);
        }, () -> hooks.add(hookEntity));
    }

    @Override
    public void removeHook(int id) {
        hooks.removeIf(hookEntity -> hookEntity.getId() == id);
    }

    @Override
    public void removeHook(HookEntity hookEntity) {
        PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.State.RETRACT_HOOK, hookEntity.getId()));
        hooks.remove(hookEntity);
    }

    @Override
    public void removeAllHooks() {
        if (hooks.isEmpty()) return;
        LOGGER.debug("Removing all hooks {}", hooks.size());
        // this is a response to a key press from the player
        // notify the server
        getOwner().ifPresent(owner -> 
                PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.State.RETRACT_ALL_HOOKS)));
        // clear hooks
        hooks.clear();
    }

    @Override
    public void shootFromRotation(float xRot, float yRot) {
        LOGGER.debug("Shooting from rotation: {}, {}", xRot, yRot);
        PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.State.SHOOT, 0, xRot, yRot));
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
    public Collection<HookEntity> getHooks() {
        return hooks;
    }

    @Override
    public void update() {
        moveVector = null;
        getOwner().ifPresent(owner -> {
            getHookData().ifPresent(hookData -> {
                if (countPulling() == 0) return;
                if (owner.getPose().equals(Pose.CROUCHING)) owner.setPose(Pose.STANDING);
                
                Vec3 ownerWaistPos = getOwnerWaist().get();
                float vPT = hookData.pullSpeed() / 20f;
                if (hookData.isCreative()) {
                    // if player going out of the box put him back in
                    VectorHelper.Box box = getBox();
                    LOGGER.debug("Box {}", box);
                    if (!box.isInside(ownerWaistPos)) {
                        moveVector = ownerWaistPos.vectorTo(box.closestPointInCube(ownerWaistPos));
                    }
                    else 
                        return;
                }
                else {
                    owner.setNoGravity(true);
                    Vec3 pullCenter = getPullCenter();
                    double x = pullCenter.x - ownerWaistPos.x;
                    double y = pullCenter.y - ownerWaistPos.y;
                    double z = pullCenter.z - ownerWaistPos.z;
                    moveVector = new Vec3(x, y, z);
                }
                // check if player is stuck against collider in a certain direction -> shouldn't pull, it causes glitches
                moveVector = reduceCollisions(moveVector);
                if (moveVector.length() > vPT) moveVector = moveVector.normalize().scale(vPT);
                if (moveVector.length() < THRESHOLD) moveVector = Vec3.ZERO;
            });
            owner.onUpdateAbilities();
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
    public double getMaxHookDistance() {
        if (hooks.isEmpty() || getOwner().isEmpty()) return 0;
      
        Player owner = getOwner().get();
        Vec3 adjustedOwnerPosition = owner.position().add(0, owner.getEyeHeight() / 1.5, 0);

        double maxDistance = 0;

        for (HookEntity hookEntity : hooks) {
            double distance = hookEntity.position().distanceTo(adjustedOwnerPosition);
            if (distance > maxDistance) maxDistance = distance;
        }
        
        return maxDistance + THRESHOLD;
    }
}

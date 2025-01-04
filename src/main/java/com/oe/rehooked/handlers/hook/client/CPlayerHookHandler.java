package com.oe.rehooked.handlers.hook.client;

import com.oe.rehooked.data.AdditionalHandlersRegistry;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.handlers.additional.def.IClientHandler;
import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.network.payloads.server.SHookPayload;
import com.oe.rehooked.utils.CurioUtils;
import com.oe.rehooked.utils.PositionHelper;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CPlayerHookHandler implements IClientPlayerHookHandler {
    private final List<HookEntity> hooks;
    private Optional<Player> owner;
    
    private Vec3 moveVector;
    private Vec3 momentum;

    private Vec3 lastPlayerPosition;
    private IClientHandler additional;
    
    public CPlayerHookHandler() {
        hooks = new ArrayList<>();
        owner = Optional.empty();
        moveVector = null;
        lastPlayerPosition = null;
    }
    
    @Override
    public void addHook(int id) {
        if (owner.isPresent()) {
            Player player = owner.get();
            Entity entity = player.level().getEntity(id);
            if (entity instanceof HookEntity hookEntity) {
                hooks.add(hookEntity);
                hookEntity.setOwner(player);
            }
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
        PacketDistributor.sendToServer(new SHookPayload(SHookPayload.State.RETRACT_HOOK.ordinal(), hookEntity.getId(), 0, 0));
        hooks.remove(hookEntity);
    }

    @Override
    public void removeAllHooks() {
        if (hooks.isEmpty()) return;
        // this is a response to a key press from the player
        // notify the server
        getOwner().ifPresent(owner -> 
                PacketDistributor.sendToServer(new SHookPayload(SHookPayload.State.RETRACT_ALL_HOOKS.ordinal(), 0, 0, 0)));
        // clear hooks
        hooks.clear();
    }

    @Override
    public void shootFromRotation(float xRot, float yRot) {
        PacketDistributor.sendToServer(new SHookPayload(SHookPayload.State.SHOOT.ordinal(), 0, xRot, yRot));
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
    public void update() {
        moveVector = null;
        getOwner().ifPresent(owner -> {
            if (additional != null) additional.update();
            getHookData().ifPresent(hookData -> {
                if (countPulling() == 0) return;
                owner.setOnGround(false);
                
                Vec3 ownerWaistPos = PositionHelper.getWaistPosition(owner);
                float vPT = hookData.pullSpeed() / 20f;
                if (hookData.isCreative()) {
                    // if player going out of the box put him back in
                    VectorHelper.Box box = getBox();
                    if (!box.isInside(ownerWaistPos)) {
                        moveVector = ownerWaistPos.vectorTo(box.closestPointInCube(ownerWaistPos));
                    }
                    else {
                        return;
                    }
                }
                else {
                    Vec3 pullCenter = getPullCenter();
                    double x = pullCenter.x - ownerWaistPos.x;
                    double y = pullCenter.y - ownerWaistPos.y;
                    double z = pullCenter.z - ownerWaistPos.z;
                    moveVector = new Vec3(x, y, z);
                }
                // check if player is stuck against collider in a certain direction -> shouldn't pull, it causes glitches
                moveVector = reduceCollisions(moveVector);
                if (moveVector.length() > vPT) moveVector = moveVector.normalize().scale(vPT);
                if (!hookData.isCreative() && moveVector.length() < THRESHOLD) moveVector = Vec3.ZERO;
            });
            owner.onUpdateAbilities();
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
    public double getMaxHookDistance() {
        if (hooks.isEmpty() || getOwner().isEmpty()) return 0;
      
        Player owner = getOwner().get();
        Vec3 adjustedOwnerPosition = PositionHelper.getWaistPosition(owner);

        double maxDistance = 0;

        for (HookEntity hookEntity : hooks) {
            double distance = hookEntity.position().distanceTo(adjustedOwnerPosition);
            if (distance > maxDistance) maxDistance = distance;
        }
        
        return maxDistance + THRESHOLD;
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
    public void onUnequip() {
        IClientPlayerHookHandler.super.onUnequip();
        additional = null;
    }

    @Override
    public void onEquip() {
        IClientPlayerHookHandler.super.onEquip();
        additional = null;
        owner.flatMap(CurioUtils::getHookType).flatMap(AdditionalHandlersRegistry::getHandler).ifPresent(cl -> {
            try {
                additional = (IClientHandler) cl.getDeclaredConstructor(IClientPlayerHookHandler.class).newInstance(this);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException ignore) {
            }
        });
    }
}

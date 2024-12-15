package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.data.IHookDataProvider;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.utils.CurioUtils;
import com.oe.rehooked.utils.PositionHelper;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Optional;

public interface ICommonPlayerHookHandler {
    double THRESHOLD = 0.2;

    void addHook(int id);
    void addHook(HookEntity hookEntity);
    void removeHook(int id);
    void removeHook(HookEntity hookEntity);
    void removeAllHooks();
    void shootFromRotation(float xRot, float yRot);
    ICommonPlayerHookHandler setOwner(Player owner);
    Optional<Player> getOwner();
    default Optional<IHookDataProvider> getHookData() {
        return getOwner()
                .flatMap(CurioUtils::GetHookType)
                .flatMap(HookRegistry::getHookData);
    }
    void update();
    boolean shouldMoveThisTick();
    Vec3 getDeltaVThisTick();
    default int countPulling() {
        int count = 0;
        for (HookEntity hookEntity : getHooks()) if (hookEntity.getState().equals(HookEntity.State.PULLING)) count++;
        return count;    
    }
    Collection<HookEntity> getHooks();
    default Vec3 getPullCenter() {
        double pulling = 0;
        double x = 0, y = 0, z = 0;
        for (HookEntity hookEntity : getHooks()) {
            if (hookEntity.getState().equals(HookEntity.State.PULLING) && hookEntity.getHitPos().isPresent()) {
                pulling++;
                Vec3 hit = hookEntity.position();
                x += hit.x;
                y += hit.y;
                z += hit.z;
            }
        }
        return new Vec3(x / pulling, y / pulling, z / pulling);
    }
    default VectorHelper.Box getBox() {
        VectorHelper.Box box = new VectorHelper.Box();
        for (HookEntity hookEntity : getHooks()) {
            if (hookEntity.getState().equals(HookEntity.State.PULLING))
                box.reassignPoints(hookEntity.position());
        }
        return box;
    }
    default Vec3 reduceCollisions(Vec3 moveVector) {
        return reduceCollisions(moveVector.x, moveVector.y, moveVector.z);
    }
    default Vec3 reduceCollisions(double dX, double dY, double dZ) {
        return getOwner().map(owner -> {
            double x = dX, y = dY, z = dZ;
            Vec3 ownerWaistPosition = PositionHelper.getWaistPosition(owner);
            // check if player is stuck against collider in a certain direction -> shouldn't pull, it causes glitches
            BlockHitResult hitResult = VectorHelper.getFromEntityAndAngle(owner, new Vec3(1, 0, 0), x, PositionHelper::getWaistPosition);
            if (hitResult.getType().equals(HitResult.Type.BLOCK) && !owner.level().getBlockState(hitResult.getBlockPos()).isAir()) {
                x = hitResult.getLocation().distanceTo(ownerWaistPosition) * Math.signum(x);
                if (Math.abs(x) <= 1) x = 0;
            }
            hitResult = VectorHelper.getFromEntityAndAngle(owner, new Vec3(0, 1, 0), y, PositionHelper::getWaistPosition);
            if (hitResult.getType().equals(HitResult.Type.BLOCK) && !owner.level().getBlockState(hitResult.getBlockPos()).isAir()) {
                y = hitResult.getLocation().distanceTo(ownerWaistPosition) * Math.signum(y);
                if (Math.abs(y) <= 1.5) y = 0;
            }
            hitResult = VectorHelper.getFromEntityAndAngle(owner, new Vec3(0, 0, 1), z, PositionHelper::getWaistPosition);
            if (hitResult.getType().equals(HitResult.Type.BLOCK) && !owner.level().getBlockState(hitResult.getBlockPos()).isAir()) {
                z = hitResult.getLocation().distanceTo(ownerWaistPosition) * Math.signum(z);
                if (Math.abs(z) <= 1) z = 0;
            }
            return new Vec3(x, y, z);
        }).orElse(null);
    }
    default void onUnequip() {
        removeAllHooks();
        update();
    }
    default void onEquip() {
        removeAllHooks();
        update();
    }
    
    default void jump() {
        getOwner().ifPresent(owner -> {
            setMomentum(getJumpVector());
            removeAllHooks();
        });
    }
    
    void setDeltaVThisTick(Vec3 dV);
    
    default Vec3 getJumpVector() {
        return getHookData().map(hookData -> {
            if (countPulling() > 0 && shouldMoveThisTick()) {
                Vec3 dVT = actualPlayerPositionChange();
                if (dVT.y < 0.75)
                    dVT = new Vec3(dVT.x, 0.75, dVT.z);
                return reduceCollisions(dVT);
            }
            return Vec3.ZERO;
        }).orElse(Vec3.ZERO);
    }
    
    Vec3 getMomentum();
    void setMomentum(Vec3 momentum);
    
    default void updateMomentum() {
        if (getMomentum() == null) return;
        if ((getMomentum().horizontalDistance() < THRESHOLD && getMomentum().y < THRESHOLD) || countPulling() > 0) {
            setMomentum(null);
            return;
        }
        getOwner().ifPresent(owner -> {
            if (owner.getAbilities().flying) {
                setMomentum(null);
                return;
            }
            double horizontalScale = 0.95;
            double verticalScale = 0.9;
            // if on ground or in water remove momentum faster
            boolean moreFriction = owner.onGround() || owner.isInWater();
            if (moreFriction) {
                horizontalScale = 0.75;
                verticalScale = 0.75;
            }
            Vec3 actualMomentum = reduceCollisions(getMomentum());
            // apply the momentum to the player and update next ticks momentum
            setDeltaVThisTick(actualMomentum);
            actualMomentum = actualMomentum.multiply(horizontalScale, verticalScale, horizontalScale);
            if (!moreFriction && !owner.getAbilities().flying) actualMomentum = actualMomentum.subtract(0, 0.08, 0);
            setMomentum(actualMomentum);
        });
    }
    
    Optional<Vec3> getLastPlayerPosition();
    
    default Vec3 actualPlayerPositionChange() {
        return getOwner().map(Player::position)
                .flatMap(currPos -> getLastPlayerPosition().map(lastPos -> lastPos.vectorTo(currPos)))
                .orElse(Vec3.ZERO);
    }
    
    void storeLastPlayerPosition();
    
    void killHook(int id);
}

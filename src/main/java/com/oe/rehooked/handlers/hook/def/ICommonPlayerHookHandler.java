package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.utils.CurioUtils;
import com.oe.rehooked.utils.PositionHelper;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
    default Optional<HookData> getHookData() {
        return getOwner()
                .flatMap(owner -> CurioUtils.GetCuriosOfType(HookItem.class, owner))
                .flatMap(CurioUtils::GetIfUnique)
                .map(ItemStack::getItem)
                .map(item -> ((HookItem) item).getHookType())
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
                Vec3 hit = hookEntity.getHitPos().get().getCenter();
                x += hit.x;
                y += hit.y;
                z += hit.z;
            }
        }
        return new Vec3(x / pulling, y / pulling, z / pulling);
    }
    default VectorHelper.Box getBox() {
        VectorHelper.Box box = new VectorHelper.Box();
        for (HookEntity hookEntity : getHooks()) 
            hookEntity.getHitPos().ifPresent(pos -> box.reassignPoints(pos.getCenter()));
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
}

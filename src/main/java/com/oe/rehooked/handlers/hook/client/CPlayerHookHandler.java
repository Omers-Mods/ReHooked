package com.oe.rehooked.handlers.hook.client;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.server.SHookCapabilityPacket;
import com.oe.rehooked.utils.CurioUtils;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

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
    public Optional<HookData> getHookData() {
        return getOwner()
                .flatMap(owner -> CurioUtils.GetCuriosOfType(HookItem.class, owner))
                .flatMap(CurioUtils::GetIfUnique)
                .map(ItemStack::getItem)
                .map(item -> ((HookItem) item).getHookType())
                .flatMap(HookRegistry::getHookData);
    }

    @Override
    public void update() {
        moveVector = null;
        getOwner().ifPresent(owner -> {
            owner.setNoGravity(false);
            getHookData().ifPresent(hookData -> {
                float vPT = hookData.pullSpeed() / 20f;
                int count = 0;
                double x = 0, y = 0, z = 0;
                Vec3 adjustedOwnerPosition = owner.position().add(0, owner.getEyeHeight() / 1.5, 0);
                for (HookEntity hookEntity : hooks) {
                    if (hookEntity.getState().equals(HookEntity.State.PULLING)) {
                        if (hookEntity.getHitPos().isPresent()) {
                            hookEntity.setPos(hookEntity.getHitPos().get().getCenter());
                            count++;
                            Vec3 center = hookEntity.getHitPos().get().getCenter();
                            x += center.x;
                            y += center.y;
                            z += center.z;
                        }
                    }
                }
                if (count == 0) return;
                x = (x / (double) count) - owner.getX();
                y = (y / (double) count) - owner.getY();
                z = (z / (double) count) - owner.getZ();
                owner.setNoGravity(true);
                owner.resetFallDistance();
                owner.setOnGround(true);
                // check if player is stuck against collider in a certain direction -> shouldn't pull, it causes glitches
                BlockHitResult hitResult = VectorHelper.getFromEntityAndAngle(owner, new Vec3(1, 0, 0), x);
                if (hitResult.getType().equals(HitResult.Type.BLOCK) && !owner.level().getBlockState(hitResult.getBlockPos()).isAir()) {
                    x = hitResult.getLocation().distanceTo(adjustedOwnerPosition) * Math.signum(x);
                    if (Math.abs(x) <= 1) x = 0;
                }
                hitResult = VectorHelper.getFromEntityAndAngle(owner, new Vec3(0, 1, 0), y);
                if (hitResult.getType().equals(HitResult.Type.BLOCK) && !owner.level().getBlockState(hitResult.getBlockPos()).isAir()) {
                    y = hitResult.getLocation().distanceTo(adjustedOwnerPosition) * Math.signum(y);
                    if (Math.abs(y) <= 1.5) y = 0;
                }
                hitResult = VectorHelper.getFromEntityAndAngle(owner, new Vec3(0, 0, 1), z);
                if (hitResult.getType().equals(HitResult.Type.BLOCK) && !owner.level().getBlockState(hitResult.getBlockPos()).isAir()) {
                    z = hitResult.getLocation().distanceTo(adjustedOwnerPosition) * Math.signum(z);
                    if (Math.abs(z) <= 1) z = 0;
                }
                moveVector = new Vec3(x, y, z);
                if (moveVector.length() > vPT) moveVector = moveVector.normalize().scale(vPT);  
                if (moveVector.length() < THRESHOLD) moveVector = Vec3.ZERO;
            });
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

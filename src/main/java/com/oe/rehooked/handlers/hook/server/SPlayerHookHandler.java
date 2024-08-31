package com.oe.rehooked.handlers.hook.server;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.client.CHookCapabilityPacket;
import com.oe.rehooked.utils.CurioUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SPlayerHookHandler implements IServerPlayerHookHandler {
    private static final Logger LOGGER = LogUtils.getLogger(); 
    
    private final List<HookEntity> hooks;
    private Optional<Player> owner;
    private Vec3 moveVector;
    
    public SPlayerHookHandler() {
        hooks = new ArrayList<>();
        owner = Optional.empty();
        moveVector = null;
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
            // discard removed entity
            getOwner().map(Player::level).map(level -> level.getEntity(id)).ifPresent(Entity::discard);
        }
    }

    @Override
    public void removeHook(HookEntity hookEntity) {
        LOGGER.debug("Removing hook by entity with id: {}", hookEntity);
        // this is a response to a request from the hook
        if (hooks.remove(hookEntity)) {
            // discard removed entity
            if (!hookEntity.isRemoved())
                hookEntity.discard();
            // notify client player
            getOwner().ifPresent(owner -> PacketHandler.sendToPlayer(
                    new CHookCapabilityPacket(CHookCapabilityPacket.State.RETRACT_HOOK, hookEntity.getId()), 
                    (ServerPlayer) owner));
        }
    }

    @Override
    public void removeAllHooks() {
        LOGGER.debug("Removing all hooks ({})", hooks.size());
        // this is a response to a request from the client
        hooks.forEach(Entity::discard);
        hooks.clear();
    }

    @Override
    public void shootFromRotation(float xRot, float yRot) {
        LOGGER.debug("Shooting from rotation: {}, {}", xRot, yRot);
        // this is a response to a client request
        getOwner().ifPresent(owner -> {
            HookEntity hookEntity = new HookEntity(owner);
            owner.level().addFreshEntity(hookEntity);
            addHook(hookEntity);
            getHookData().ifPresent(hookData ->
                    hookEntity.shootFromRotation(owner, xRot, yRot, 0, hookData.speed() / 20f, 0));
        });
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
                for (HookEntity hookEntity : hooks) {
                    if (hookEntity.getState().equals(HookEntity.State.PULLING)) {
                        count++;
                        Vec3 vectorTo = owner.position().vectorTo(hookEntity.position());
                        // todo: maybe scale the vector down based on how close the player is to the hook
                        // todo: if the player is closer the hooks pull should be less strong
                        x += vectorTo.x;
                        y += vectorTo.y;
                        z += vectorTo.z;
                    }
                }
                if (count == 0) return;
                owner.setNoGravity(true);
                Vec3 baseVector = new Vec3(x, y, z);
                if (baseVector.length() < THRESHOLD) return;
                moveVector = baseVector.normalize().scale(vPT * count);
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
}

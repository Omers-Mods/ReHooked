package com.oe.rehooked.handlers.hook.server;

import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.client.CHookCapabilityPacket;
import com.oe.rehooked.utils.CurioUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SPlayerHookHandler implements ICommonPlayerHookHandler {
    private final List<HookEntity> hooks;
    private Optional<Player> owner;
    
    public SPlayerHookHandler() {
        hooks = new ArrayList<>();
        owner = Optional.empty();
    }

    @Override
    public void addHook(int id) {
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
        // hooks will always be added on the server first
        hooks.add(hookEntity);
        getOwner().ifPresent(owner -> PacketHandler.sendToPlayer(
                new CHookCapabilityPacket(CHookCapabilityPacket.State.ADD_HOOK, hookEntity.getId()), 
                (ServerPlayer) owner));
    }

    @Override
    public void removeHook(int id) {
        // this is a response to a request from the client
        if (hooks.removeIf(hookEntity -> hookEntity.getId() == id)) {
            // discard removed entity
            getOwner().map(Player::level).map(level -> level.getEntity(id)).ifPresent(Entity::discard);
        }
    }

    @Override
    public void removeHook(HookEntity hookEntity) {
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
        // this is a response to a request from the client
        hooks.forEach(Entity::discard);
        hooks.clear();
    }

    @Override
    public void shootFromRotation(float xRot, float yRot) {
        // todo:
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
    public ICommonPlayerHookHandler copyOnDeath(ICommonPlayerHookHandler other) {
        // todo: make sure I don't need this and delete it
        // I'm 99.9% sure this won't be needed
        return this;
    }

    @Override
    public void update() {
        // todo:
    }

    @Override
    public boolean shouldMoveThisTick() {
        // todo:
        return false;
    }

    @Override
    public Vec3 getDeltaVThisTick() {
        // todo:
        return null;
    }
}

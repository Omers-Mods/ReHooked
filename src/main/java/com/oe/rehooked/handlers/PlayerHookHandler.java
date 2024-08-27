package com.oe.rehooked.handlers;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.capabilities.hooks.IPlayerHookHandler;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.hook.HookEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class PlayerHookHandler implements IPlayerHookHandler {
    private List<HookEntity> playerHooks;
    private Player owner;
    private String hookType;
    
    public PlayerHookHandler() {
        playerHooks = new ArrayList<>();
        owner = null;
        hookType = "";
    }
    
    @Override
    public void removeHook(HookEntity hook) {
        if (!hook.isRemoved())
            hook.remove(Entity.RemovalReason.DISCARDED);
        playerHooks.remove(hook);
    }
    
    @Override
    public void removeAllHooks() {
        for (HookEntity hook : playerHooks)
            removeHook(hook);
    }
    
    @Override
    public void shootHook() {
        HookRegistry.getHookData(hookType).ifPresent(hookData -> {
            if (hookData.count() <= 0) {
                ReHookedMod.LOGGER.debug("Can't shoot hook because player doesn't have item!");
                return;
            }
            if (playerHooks.size() == hookData.count()) {
                ReHookedMod.LOGGER.debug("Player at max hooks, clearing oldest before shooting!");
                removeHook(playerHooks.get(0));
            }
            HookEntity hookEntity = new HookEntity(owner.level(), owner);
            hookEntity.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F,
                    hookData.speed(), 0.0f,
                    (int)  (hookData.range() / (hookData.speed() / 20.0f)));
            owner.level().addFreshEntity(hookEntity);
        });
    } 
    
    @Override
    public IPlayerHookHandler hookType(String hookType) {
        this.hookType = hookType;
        removeAllHooks();
        return this;
    }

    @Override
    public String getHookType() {
        return hookType;
    }

    @Override
    public Collection<HookEntity> getPlayerHooks() {
        return playerHooks;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public IPlayerHookHandler owner(Player owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public void copyFrom(IPlayerHookHandler other) {
        this.hookType = other.getHookType();
        this.playerHooks = new ArrayList<>(other.getPlayerHooks());
        this.owner = other.getOwner();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (owner != null) {
            tag.putUUID("uuid", owner.getUUID());
            tag.putString("hook_type", hookType);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("uuid")) {
            UUID playerUUID = nbt.getUUID("uuid");
            if (Minecraft.getInstance().level.isClientSide()) return;
            Iterable<ServerLevel> allLevels = Minecraft.getInstance().player.getServer().getAllLevels();
            allLevels.forEach(level -> Optional.ofNullable(level.getEntity(playerUUID))
                    .ifPresent(player -> owner = (Player) player));
            if (owner != null) {
                hookType = nbt.getString("hook_type");
                playerHooks = new ArrayList<>();
                allLevels.forEach(level -> playerHooks.addAll(
                        level.getEntitiesOfClass(HookEntity.class, AABB.of(BoundingBox.infinite()),
                                entity -> playerUUID.equals(entity.getOwner().getUUID()))
                ));
            }
        }
    }
}

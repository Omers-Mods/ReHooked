package com.oe.rehooked.handlers;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.capabilities.hooks.IPlayerHookHandler;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.utils.DistUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
    public void removeHook(int id) {
        if (id < playerHooks.size() && id >= 0) {
            HookEntity hook = playerHooks.get(id);
            if (!hook.isRemoved())
                hook.discard();
            playerHooks.remove(id);
        }
    }

    @Override
    public void removeHook(HookEntity hook) {
        if (!hook.isRemoved())
            hook.discard();
        playerHooks.remove(hook);
    }
    
    @Override
    public void removeAllHooks() {
        ReHookedMod.LOGGER.debug("Removing all hooks!");
        for (int i = 0; i < playerHooks.size(); i++) removeHook(i);
    }
    
    @Override
    public void shootHook() {
        if (owner == null) return;
        HookRegistry.getHookData(hookType).ifPresent(hookData -> {
            if (hookData.count() <= 0) {
                owner.sendSystemMessage(Component.literal("Can't shoot hook because player doesn't have item!"));
                return;
            }
            if (playerHooks.size() == hookData.count()) {
                owner.sendSystemMessage(Component.literal("Player at max hooks, clearing oldest before shooting!"));
                removeHook(0);
            }
            owner.sendSystemMessage(Component.literal("Shooting hook!"));
            HookEntity hookEntity = new HookEntity(owner);
            hookEntity.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F,
                    hookData.speed(), 0.0f,
                    (int)  (hookData.range() / (hookData.speed() / 20.0f)));
            owner.level().addFreshEntity(hookEntity);
            playerHooks.add(hookEntity);
        });
        owner.sendSystemMessage(Component.literal("Player: " + owner.getDisplayName().getString()));
        owner.sendSystemMessage(Component.literal("Hook Type: " + hookType));
        owner.sendSystemMessage(Component.literal("Current hooks: " + playerHooks.size()));
    } 
    
    @Override
    public IPlayerHookHandler hookType(String hookType) {
        if (hookType.equals(this.hookType)) return this;
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
        if (owner != null && owner.equals(this.owner)) return this;
        this.owner = owner;
        return this;
    }

    @Override
    public void copyFrom(IPlayerHookHandler other) {
        this.hookType = other.getHookType();
        this.playerHooks = new ArrayList<>(other.getPlayerHooks());
        this.owner = other.getOwner();
        if (owner != null) owner.sendSystemMessage(Component.literal("Copied from another capability"));
    }
    
    public void serializeNBT(CompoundTag tag) {
        if (owner != null) {
            owner.sendSystemMessage(Component.literal("Serializing hook capability"));
            tag.putUUID("uuid", owner.getUUID());
            tag.putString("hook_type", hookType);
        }
    }
    
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("uuid")) {
            UUID playerUUID = nbt.getUUID("uuid");
            if (DistUtil.IsClient()) return;
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            owner = server.getPlayerList().getPlayer(playerUUID);
            if (owner != null) {
                owner.sendSystemMessage(Component.literal("Deserializing hook capability"));
                hookType = nbt.getString("hook_type");
                playerHooks = new ArrayList<>();
                server.getAllLevels().forEach(level -> playerHooks.addAll(
                        level.getEntitiesOfClass(HookEntity.class, AABB.of(BoundingBox.infinite()),
                                entity -> playerUUID.equals(entity.getOwner().getUUID()))
                ));
                owner.sendSystemMessage(Component.literal("Found " + playerHooks.size() + " hooks"));
            }
        }
    }
}

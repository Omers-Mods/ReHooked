package com.oe.rehooked.handlers;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.capabilities.hooks.IPlayerHookHandler;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.hook.HookEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerHookHandler implements IPlayerHookHandler {
    public static final double THRESHOLD = 0.5; 
    
    private List<HookEntity> playerHooks;
    private Player owner;
    private String hookType;
    private Vec3 moveVector;
    int ticksToCoverDistance;
    
    public PlayerHookHandler() {
        playerHooks = new ArrayList<>();
        owner = null;
        hookType = "";
        moveVector = Vec3.ZERO;
        ticksToCoverDistance = 0;
    }

    @Override
    public void removeHook(int id) {
        if (id < playerHooks.size() && id >= 0) {
            HookEntity hook = playerHooks.get(id);
            boolean update = hook.getState() == HookEntity.State.HIT;
            if (!hook.isRemoved()) {
                hook.discard();
            }
            playerHooks.remove(id);
            if (update)
                update();
        }
    }

    @Override
    public void removeHook(HookEntity hook) {
        if (hook.getState() == HookEntity.State.HIT)
            update();
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
            if (hookData.speed() == Double.MAX_VALUE)
                hookEntity.shootInstant(owner, hookData.range());
            else 
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
        this.playerHooks = new ArrayList<>();
        owner(other.getOwner());
        hookType(other.getHookType());
        if (owner != null) owner.sendSystemMessage(Component.literal("Copied from another capability"));
    }
    
    public void serializeNBT(CompoundTag tag) {
        if (owner != null) {
            owner.sendSystemMessage(Component.literal("Serializing hook capability"));
            tag.putUUID("uuid", owner.getUUID());
            tag.putString("hook_type", hookType);
            tag.putInt("ticks", ticksToCoverDistance);
            if (moveVector != null) {
                tag.putDouble("x", moveVector.x);
                tag.putDouble("y", moveVector.y);
                tag.putDouble("z", moveVector.z);
            }
            else {
                tag.putDouble("x", 0);
                tag.putDouble("y", 0);
                tag.putDouble("z", 0);
            }
        }
    }
    
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("uuid")) {
            UUID playerUUID = nbt.getUUID("uuid");
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
            ticksToCoverDistance = nbt.getInt("ticks");
            moveVector = new Vec3(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
        }
    }

    @Override
    public void update() {
        double x = 0;
        double y = 0;
        double z = 0;
        int count = 0;
        for (HookEntity hook : playerHooks) {
            if (hook.getState() == HookEntity.State.HIT) {
                count++;
                x += hook.getX();
                y += hook.getY();
                z += hook.getZ();
            }
        }
        if (count == 0) {
            moveVector = null;
            return;
        }
        // need to move player in the direction of the center of all hooks
        moveVector = new Vec3(x / count, y / count, z / count).subtract(owner.position());
        HookRegistry.getHookData(hookType).ifPresent(data -> {
            ticksToCoverDistance = (int) (moveVector.length() / (data.pullSpeed() / 20));
            moveVector = moveVector.scale(((double) 1) / ((double) ticksToCoverDistance));
        });
    }

    @Override
    public Vec3 getMoveThisTick() {
        if (ticksToCoverDistance == 0) {
            moveVector = null;
            return Vec3.ZERO;
        }
        ticksToCoverDistance--;
        return moveVector;
    }

    @Override
    public boolean shouldMoveThisTick() {
        return moveVector != null && 
                !moveVector.equals(Vec3.ZERO) && 
                moveVector.scale(ticksToCoverDistance).length() > THRESHOLD;
    }
}

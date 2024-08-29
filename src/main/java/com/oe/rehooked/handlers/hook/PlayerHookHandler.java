package com.oe.rehooked.handlers.hook;

import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

import java.util.*;

public class PlayerHookHandler implements IPlayerHookHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
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
        LOGGER.debug("Removing single hook by id");
        if (id < playerHooks.size() && id >= 0) {
            HookEntity hook = playerHooks.get(id);
            if (!hook.isRemoved()) hook.setState(HookEntity.State.RETRACTING.ordinal());
            playerHooks.remove(id);
        }
    }

    @Override
    public void removeHook(HookEntity hook) {
        LOGGER.debug("Removing single hook by entity");
        if (!hook.isRemoved()) hook.setState(HookEntity.State.RETRACTING.ordinal());
        playerHooks.remove(hook);
    }
    
    @Override
    public void removeAllHooks() {
        LOGGER.debug("Removing all hooks!");
        for (int i = playerHooks.size() - 1; i >= 0; i--) removeHook(i);
    }
    
    @Override
    public void shootHook() {
        if (owner == null) return;
        LOGGER.debug("Shooting hook");
        HookRegistry.getHookData(hookType).ifPresent(hookData -> {
            // doesn't have hook capacity at all
            if (hookData.count() <= 0) return;
            // else if just doesn't have more room for hooks, delete oldest and continue
            if (playerHooks.size() == hookData.count()) removeHook(0);
            // spawn and add new hook
            HookEntity hookEntity = new HookEntity(owner, hookType);
            owner.level().addFreshEntity(hookEntity);
            playerHooks.add(hookEntity);
        });
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
    public IPlayerHookHandler copyFrom(IPlayerHookHandler other) {
        LOGGER.debug("Copied from another");
        this.playerHooks = new ArrayList<>();
        owner(other.getOwner());
        hookType(other.getHookType());
        return this;
    }
    
    public void serializeNBT(CompoundTag tag) {
        if (owner != null) {
            LOGGER.debug("Serializing hook capability");
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
                LOGGER.debug("Deserializing hook capability");
                hookType = nbt.getString("hook_type");
                playerHooks = new ArrayList<>();
                server.getAllLevels().forEach(level -> playerHooks.addAll(
                        level.getEntitiesOfClass(HookEntity.class, AABB.of(BoundingBox.infinite()),
                                entity -> entity.getOwner().flatMap(player -> Optional.of(player.getUUID().equals(playerUUID))).orElse(false))
                ));
                owner.sendSystemMessage(Component.literal("Found " + playerHooks.size() + " hooks"));
            }
            ticksToCoverDistance = nbt.getInt("ticks");
            moveVector = new Vec3(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
        }
    }

    @Override
    public void update() {
        LOGGER.debug("Updating handler");
        owner.setNoGravity(false);
        HookRegistry.getHookData(hookType).ifPresent(data -> {
            float vPT = data.pullSpeed() / 20.0f;
            int count = 0;
            moveVector = Vec3.ZERO;
            for (HookEntity hook : playerHooks) {
                if (hook.getState() == HookEntity.State.PULLING.ordinal()) {
                    count++;
                    moveVector = moveVector.add(owner.position().vectorTo(hook.position()));
                }
            }
            if (count == 0) {
                moveVector = null;
                return;
            }
            moveVector = moveVector.normalize().scale(vPT);
            
            ticksToCoverDistance = (int) (moveVector.length() / (data.pullSpeed() / 20));
            owner.setNoGravity(true);
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

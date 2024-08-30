package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.capabilities.hooks.IPlayerHookHandler;
import com.oe.rehooked.capabilities.hooks.PlayerHookCapabilityProvider;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.entities.hook.HookEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

public interface ICommonPlayerHookHandler extends INBTSerializable<CompoundTag> {
    String HOOKS_TAG = "HOOKS_TAG";
    void removeHook(int id);
    void removeHook(HookEntity hookEntity);
    void removeAllHooks();
    void shootFromRotation(float xRot, float yRot);
    IPlayerHookHandler setOwner(Player owner);
    Optional<Player> getOwner();
    Optional<HookData> getHookData();
    IPlayerHookHandler copyOnDeath(IPlayerHookHandler other);
    void update();
    boolean shouldMoveThisTick();
    Vec3 getDeltaVThisTick();
    static LazyOptional<IPlayerHookHandler> FromPlayer(Player player) {
        return player.getCapability(PlayerHookCapabilityProvider.PLAYER_HOOK_HANDLER);
    }
}

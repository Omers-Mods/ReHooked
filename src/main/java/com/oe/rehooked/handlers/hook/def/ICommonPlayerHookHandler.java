package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.capabilities.hooks.PlayerHookCapabilityProvider;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.entities.hook.HookEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

@AutoRegisterCapability
public interface ICommonPlayerHookHandler {
    void addHook(int id);
    void addHook(HookEntity hookEntity);
    void removeHook(int id);
    void removeHook(HookEntity hookEntity);
    void removeAllHooks();
    void shootFromRotation(float xRot, float yRot);
    ICommonPlayerHookHandler setOwner(Player owner);
    Optional<Player> getOwner();
    Optional<HookData> getHookData();
    ICommonPlayerHookHandler copyOnDeath(ICommonPlayerHookHandler other);
    void update();
    boolean shouldMoveThisTick();
    Vec3 getDeltaVThisTick();
    static LazyOptional<ICommonPlayerHookHandler> FromPlayer(Player player) {
        if (player instanceof LocalPlayer)
            return player.getCapability(PlayerHookCapabilityProvider.CLIENT_HOOK_HANDLER);
        else if (player instanceof ServerPlayer)
            return player.getCapability(PlayerHookCapabilityProvider.SERVER_HOOK_HANDLER);
        else
            return LazyOptional.empty();
    }
}

package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.capabilities.hooks.ClientHookCapabilityProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public interface IClientPlayerHookHandler extends ICommonPlayerHookHandler {
    static LazyOptional<IClientPlayerHookHandler> FromPlayer(Player player) {
        return player.getCapability(ClientHookCapabilityProvider.CLIENT_HOOK_HANDLER);
    }
}

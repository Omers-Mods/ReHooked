package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.capabilities.hooks.ServerHookCapabilityProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public interface IServerPlayerHookHandler extends ICommonPlayerHookHandler {
    static LazyOptional<IServerPlayerHookHandler> FromPlayer(Player player) {
        return player.getCapability(ServerHookCapabilityProvider.SERVER_HOOK_HANDLER);
    }
}

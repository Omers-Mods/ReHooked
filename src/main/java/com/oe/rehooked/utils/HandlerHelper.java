package com.oe.rehooked.utils;

import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class HandlerHelper {
    public static Optional<? extends ICommonPlayerHookHandler> getHookHandler(Player player) {
        if (player == null) return Optional.empty();
        if (player.level().isClientSide()) return IClientPlayerHookHandler.fromPlayer(player);
        return IServerPlayerHookHandler.fromPlayer(player);
    }
}

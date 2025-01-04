package com.oe.rehooked.network.handlers.server;

import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import com.oe.rehooked.network.payloads.server.SHookPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class SHookPayloadHandler {
    public static void handle(final SHookPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Optional<IServerPlayerHookHandler> optHandler = IServerPlayerHookHandler.fromPlayer(player);
            if (optHandler.isPresent()) {
                IServerPlayerHookHandler handler = optHandler.get();
                handler.setOwner(player);
                switch (SHookPayload.State.get(payload.state())) {
                    case SHOOT -> handler.shootFromRotation(payload.xRot(), payload.yRot());
                    case RETRACT_HOOK -> handler.removeHook(payload.id());
                    case RETRACT_ALL_HOOKS -> handler.removeAllHooks();
                    case FORCE_UPDATE -> handler.update();
                    case JUMP -> handler.jump();
                    case KILL -> handler.killHook(payload.id());
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal(e.getMessage()));
            return null;
        });
    }
}

package com.oe.rehooked.network.handlers.client;

import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.network.payloads.client.CHookPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CHookPayloadHandler {
    public static void handle(final CHookPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> Wrapper.handle(payload)).exceptionally(e -> {
            context.disconnect(Component.literal(e.getMessage()));
            return null;
        });
    }
    
    private static class Wrapper {
        public static void handle(final CHookPayload payload) {
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            IClientPlayerHookHandler.fromPlayer(player).ifPresent(handler -> {
                switch (CHookPayload.State.get(payload.state())) {
                    case ADD_HOOK -> handler.addHook(payload.id());
                    case RETRACT_HOOK -> handler.removeHook(payload.id());
                    case RETRACT_ALL_HOOKS -> handler.removeAllHooks();
                    case FORCE_UPDATE -> handler.update();
                }
            });
        }
    }
}

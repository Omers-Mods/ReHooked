package com.oe.rehooked.network.handlers.client;

import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.network.payloads.client.CHookPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class CHookPayloadHandler {
    public static void handle(final CHookPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            Optional<IClientPlayerHookHandler> optHandler = IClientPlayerHookHandler.fromPlayer(player);
            if (optHandler.isPresent()) {
                IClientPlayerHookHandler handler = optHandler.get();
                switch (CHookPayload.State.get(payload.state())) {
                    case ADD_HOOK -> handler.addHook(payload.id());
                    case RETRACT_HOOK -> handler.removeHook(payload.id());
                    case RETRACT_ALL_HOOKS -> handler.removeAllHooks();
                    case FORCE_UPDATE -> handler.update();
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal(e.getMessage()));
            return null;
        });
    }
}

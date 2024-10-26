package com.oe.rehooked.network.event;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.network.handlers.client.CHookPayloadHandler;
import com.oe.rehooked.network.handlers.server.SHookPayloadHandler;
import com.oe.rehooked.network.payloads.client.CHookPayload;
import com.oe.rehooked.network.payloads.server.SHookPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkEventListener {
    public static final String NET_VERSION = "1"; 
    
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(NET_VERSION);
        registrar.playToClient(
                CHookPayload.TYPE,
                CHookPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        CHookPayloadHandler::handle,
                        (cHookPayload, iPayloadContext) -> {}
                )
        ).playToServer(
                SHookPayload.TYPE,
                SHookPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (sHookPayload, iPayloadContext) -> {},
                        SHookPayloadHandler::handle
                )
        );
    }
}

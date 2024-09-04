package com.oe.rehooked.network.handlers;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.network.packets.client.CHookCapabilityPacket;
import com.oe.rehooked.network.packets.server.SHookCapabilityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(ReHookedMod.MOD_ID, "main"))
            .serverAcceptedVersions(s -> true)
            .clientAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
    
    private static final Map<Class<?>, BiConsumer<? extends IHandler, Supplier<NetworkEvent.Context>>> handlers = new HashMap<>();
    
    public static <T, E extends IHandler> void addHandler(Class<T> clazz, BiConsumer<E, Supplier<NetworkEvent.Context>> handler) {
        handlers.put(clazz, handler);
    }
    
    private static <T> void handle(T packet, Supplier<NetworkEvent.Context> context) {
        BiConsumer<T, Supplier<NetworkEvent.Context>> consumer = (BiConsumer<T, Supplier<NetworkEvent.Context>>) handlers.get(packet.getClass());
        if (consumer != null) consumer.accept(packet, context);
        else context.get().setPacketHandled(true);
    }
    
    public static void register() {
        ReHookedMod.LOGGER.debug("Packet handler register started...");
        
        INSTANCE.messageBuilder(SHookCapabilityPacket.class, NetworkDirection.PLAY_TO_SERVER.ordinal())
                .encoder(SHookCapabilityPacket::encode)
                .decoder(SHookCapabilityPacket::new)
                .consumerMainThread(SHookCapabilityPacket::handle)
                .add();

        ReHookedMod.LOGGER.debug("Registered ServerHookPacket");
        
        INSTANCE.messageBuilder(CHookCapabilityPacket.class, NetworkDirection.PLAY_TO_CLIENT.ordinal())
                .encoder(CHookCapabilityPacket::encode)
                .decoder(CHookCapabilityPacket::new)
                .consumerMainThread(PacketHandler::handle)
                .add();

        ReHookedMod.LOGGER.debug("Registered ClientHookPacket");
        
//        INSTANCE.messageBuilder(CPushPlayerPacket.class, NetworkDirection.PLAY_TO_CLIENT.ordinal())
//                .encoder(CPushPlayerPacket::encode)
//                .decoder(CPushPlayerPacket::new)
//                .consumerMainThread(CPushPlayerPacket::handle)
//                .add();
    }
    
    public static void sendToServer(Object msg) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }
    
    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
    
    public static void sendToAllClients(Object msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}

package com.oe.rehooked.network.handlers;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.network.packets.client.CHookCapabilityPacket;
import com.oe.rehooked.network.packets.client.CSoundPacket;
import com.oe.rehooked.network.packets.server.SHookCapabilityPacket;
import com.oe.rehooked.network.packets.server.SSoundPacket;
import com.oe.rehooked.sound.ServerSoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
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
    
    public static void Init() {
        LOGGER.debug("Packet handler register started...");
        int id = 0;
        
        INSTANCE.messageBuilder(SHookCapabilityPacket.class, id++)
                .encoder(SHookCapabilityPacket::encode)
                .decoder(SHookCapabilityPacket::new)
                .consumerMainThread(SHookCapabilityPacket::handle)
                .add();

        LOGGER.debug("Registered ServerHookPacket");
        
        INSTANCE.messageBuilder(CHookCapabilityPacket.class, id++)
                .encoder(CHookCapabilityPacket::encode)
                .decoder(CHookCapabilityPacket::new)
                .consumerMainThread(PacketHandler::handle)
                .add();

        LOGGER.debug("Registered ClientHookPacket");
        
        INSTANCE.messageBuilder(SSoundPacket.class, id++)
                .encoder(SSoundPacket::encode)
                .decoder(SSoundPacket::new)
                .consumerMainThread(ServerSoundManager::handlePacket)
                .add();

        LOGGER.debug("Registered ServerSoundPacket");

        INSTANCE.messageBuilder(CSoundPacket.class, id++)
                .encoder(CSoundPacket::encode)
                .decoder(CSoundPacket::new)
                .consumerMainThread(PacketHandler::handle)
                .add();

        LOGGER.debug("Registered ClientSoundPacket");
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
    
    public static void sendToClientsInRange(ServerLevel level, Vec3 position, int range, Object message) {
        level.players().forEach(player -> {
            if (player.distanceToSqr(position) <= range * range) 
                sendToPlayer(message, player);
        });
    }
    
    public static void sendToClientsInLevel(ServerLevel level, Object message) {
        LOGGER.debug("Sending message {} to all players in dimension {}", message, level);
        level.players().forEach(player -> sendToPlayer(message, player));
    }
}

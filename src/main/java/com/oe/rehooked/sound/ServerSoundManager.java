package com.oe.rehooked.sound;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.client.CSoundPacket;
import com.oe.rehooked.network.packets.server.SSoundPacket;
import com.oe.rehooked.sound.info.def.IKeepAliveInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerSoundManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int LEVEL_CHECK_INTERVAL = 10;

    private ServerSoundManager() {}
    
    private static final Map<ResourceKey<Level>, Long> lastLevelCheck = new HashMap<>();
    private static final Map<ResourceKey<Level>, Map<UUID, IKeepAliveInfo>> levelSoundInfo = new HashMap<>();
    
    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide() || event.phase != TickEvent.Phase.END) return;
        ServerLevel level = (ServerLevel) event.level;
        ResourceKey<Level> dim = level.dimension();
        // if the interval hasn't passed yet, don't check again
        if (lastLevelCheck.computeIfAbsent(dim, key -> level.getGameTime()) > (level.getGameTime() - LEVEL_CHECK_INTERVAL)) 
            return;
        // update last check
        lastLevelCheck.put(dim, level.getGameTime());
        // go over the level sounds and terminate if necessary
        levelSoundInfo.computeIfAbsent(dim, k -> new HashMap<>()).entrySet().removeIf(entry -> {
            if (entry.getValue().keepAlive(level)) return false;
            entry.getValue().onKill();
            sendStopMessage(entry.getKey());
            return true;
        });
    }
    
    private static void sendStopMessage(UUID uuid) {
        PacketHandler.sendToAllClients(CSoundPacket.createRemovePacket(uuid));
    }

    public static void handlePacket(SSoundPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            LOGGER.debug("Started handling packet");
            ServerPlayer sender = context.get().getSender();
            if (sender == null) return;
            switch (packet.state) {
                case ADD -> addSound((ServerLevel) sender.level(), packet);
                case REMOVE -> removeSound((ServerLevel) sender.level(), packet);
            }
        });
        context.get().setPacketHandled(true);
    }
    
    private static void addSound(ServerLevel level, SSoundPacket packet) {
        Map<UUID, IKeepAliveInfo> levelSounds = levelSoundInfo.computeIfAbsent(level.dimension(), k -> new HashMap<>());
        UUID soundId = UUID.randomUUID();
        IKeepAliveInfo info = packet.type.create(packet);
        levelSounds.put(soundId, info);
        LOGGER.debug("Added sound {} with keep alive of {}", soundId, info.getClass());
        PacketHandler.sendToClientsInLevel(level, info.createSyncPacket(soundId));
    }
    
    public static UUID addSound(ServerLevel level, IKeepAliveInfo info) {
        Map<UUID, IKeepAliveInfo> levelSounds = levelSoundInfo.computeIfAbsent(level.dimension(), k -> new HashMap<>());
        UUID soundId = UUID.randomUUID();
        levelSounds.put(soundId, info);
        LOGGER.debug("Added sound {} with keep alive of {}", soundId, info.getClass());
        PacketHandler.sendToClientsInLevel(level, info.createSyncPacket(soundId));
        return soundId;
    }
    
    private static void removeSound(ServerLevel level, SSoundPacket packet) {
        Map<UUID, IKeepAliveInfo> levelSounds = levelSoundInfo.computeIfAbsent(level.dimension(), k -> new HashMap<>());
        IKeepAliveInfo removed;
        if (levelSounds.containsKey(packet.uuid)) {
            removed = levelSounds.remove(packet.uuid);
            removed.onKill();
            removed.createSyncPacket(packet.uuid);
        }
    }
    
    public static boolean isSoundActive(Level level, UUID uuid) {
        return levelSoundInfo.containsKey(level.dimension()) && 
                levelSoundInfo.get(level.dimension()).containsKey(uuid);
    }
}

package com.oe.rehooked.sound;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.client.CSoundPacket;
import com.oe.rehooked.network.packets.server.SSoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientSoundManager {
    private static final int LEVEL_CHECK_INTERVAL = 10;
    
    private ClientSoundManager() {}
    
    private static final Map<UUID, SoundInstance> sounds = new HashMap<>();
    private static long lastChecked = 0;
    
    public static SoundManager soundManager() {
        return Minecraft.getInstance().getSoundManager();
    }
    
    public static void stopSound(UUID uuid) {
        if (sounds.containsKey(uuid)) {
            soundManager().stop(sounds.remove(uuid));
        }
    }
    
    public static void playSound(UUID uuid, SoundInstance sound) {
        soundManager().play(sound);
        sounds.put(uuid, sound);
    }
    
    public static void playSound(UUID uuid, SoundEvent event, int entityId, float volume, float pitch) {
        ClientLevel level = Minecraft.getInstance().level;
        // if level is null exit early
        if (level == null) return;
        // if entity not found exit early
        Entity entity = level.getEntity(entityId);
        if (entity == null) return;
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getId() == entityId)
            playSound(uuid, new SimpleSoundInstance(event.getLocation(), entity.getSoundSource(), volume, pitch, 
                    SoundInstance.createUnseededRandom(), true, 0, SoundInstance.Attenuation.NONE, 
                    0, 0, 0, true));
        else 
            playSound(uuid, new EntityBoundSoundInstance(event, entity.getSoundSource(), volume, pitch, entity, 0));
    }
    
    public void clearSounds() {
        sounds.forEach((uuid, sound) -> soundManager().stop(sound));
        sounds.clear();
    }
    
    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (!sounds.isEmpty() && lastChecked < event.level.getGameTime() - LEVEL_CHECK_INTERVAL) {
            lastChecked = event.level.getGameTime();
            sounds.entrySet().removeIf(entry -> {
                if (!soundManager().isActive(entry.getValue())) {
                    PacketHandler.sendToServer(SSoundPacket.createRemovePacket(entry.getKey()));
                    return true;
                }
                return false;
            });
        }
    }
    
    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        sounds.clear();
        lastChecked = 0;
    }

    public static void handlePacket(CSoundPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            switch (packet.state) {
                case ADD -> playSound(packet.uuid, packet.soundRegObj.get(), packet.entityId, packet.volume, packet.pitch);
                case REMOVE -> stopSound(packet.uuid);
            }
        });
        context.get().setPacketHandled(true);
    }
}

package com.oe.rehooked.network.packets.client;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.sound.ReHookedSounds;
import com.oe.rehooked.sound.info.def.SoundState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.UUID;

public class CSoundPacket {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public UUID uuid;
    public SoundState state;
    public RegistryObject<SoundEvent> soundRegObj;
    public int entityId;
    public float volume;
    public float pitch;

    public CSoundPacket(UUID uuid, SoundState state, RegistryObject<SoundEvent> soundRegObj, int entityId, float volume, float pitch) {
        this.uuid = uuid;
        this.state = state;
        this.soundRegObj = soundRegObj;
        this.entityId = entityId;
        this.volume = volume;
        this.pitch = pitch;
    }
    
    public static CSoundPacket createAddPacket(UUID uuid, RegistryObject<SoundEvent> soundRegObj, int entityId, float volume, float pitch) {
        return new CSoundPacket(uuid, SoundState.ADD, soundRegObj, entityId, volume, pitch);
    }
    
    public static CSoundPacket createRemovePacket(UUID uuid) {
        return new CSoundPacket(uuid, SoundState.REMOVE, null, 0, 0, 0);
    }
    
    public CSoundPacket(FriendlyByteBuf buf) {
        LOGGER.debug("Decoding...");
        uuid = buf.readUUID();
        LOGGER.debug("UUID: {}", uuid);
        state = SoundState.values()[buf.readInt()];
        LOGGER.debug("SoundState: {}", state);
        if (state.equals(SoundState.ADD)) {
            soundRegObj = ReHookedSounds.GetEvent(buf.readInt());
            LOGGER.debug("Sound: {}", soundRegObj.get().getLocation());
            entityId = buf.readInt();
            LOGGER.debug("EntityId: {}", entityId);
            volume = buf.readFloat();
            LOGGER.debug("Volume: {}", volume);
            pitch = buf.readFloat();
            LOGGER.debug("Pitch: {}", pitch);
        }
    }
    
    public void encode(FriendlyByteBuf buf) {
        LOGGER.debug("Encoding...");
        buf.writeUUID(uuid);
        buf.writeInt(state.ordinal());
        if (state.equals(SoundState.ADD)) {
            buf.writeInt(ReHookedSounds.GetIndex(soundRegObj));
            buf.writeInt(entityId);
            buf.writeFloat(volume);
            buf.writeFloat(pitch);
        }
    }
}

package com.oe.rehooked.network.packets.server;

import com.oe.rehooked.sound.ReHookedSounds;
import com.oe.rehooked.sound.info.InfoType;
import com.oe.rehooked.sound.info.def.SoundState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

public class SSoundPacket {
    public SoundState state;
    public InfoType type;
    public RegistryObject<SoundEvent> soundRegObj;
    public int entityId;
    public float volume;
    public float pitch;
    public UUID uuid;
    
    public SSoundPacket(SoundState state, InfoType type, RegistryObject<SoundEvent> soundRegObj, int entityId, float volume, float pitch, UUID uuid) {
        this.state = state;
        this.type = type;
        this.soundRegObj = soundRegObj;
        this.entityId = entityId;
        this.volume = volume;
        this.pitch = pitch;
        this.uuid = uuid;
    }
    
    public static SSoundPacket createAddPacket(InfoType type, RegistryObject<SoundEvent> soundRegObj, int entityId, float volume, float pitch) {
        return new SSoundPacket(SoundState.ADD, type, soundRegObj, entityId, volume, pitch, null);
    }
    
    public static SSoundPacket createRemovePacket(UUID uuid) {
        return new SSoundPacket(SoundState.REMOVE, null, null, 0, 0, 0, uuid);
    }
    
    public SSoundPacket(FriendlyByteBuf buf) {
        this.state = SoundState.values()[buf.readInt()];
        if (state.equals(SoundState.ADD)) {
            this.type = InfoType.values()[buf.readInt()];
            this.soundRegObj = ReHookedSounds.GetEvent(buf.readInt());
            this.entityId = buf.readInt();
            this.volume = buf.readFloat();
            this.pitch = buf.readFloat();
        }
        else if (state.equals(SoundState.REMOVE)) {
            this.uuid = buf.readUUID();
        }
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(state.ordinal());
        if (state.equals(SoundState.ADD)) {
            buf.writeInt(type.ordinal());
            buf.writeInt(ReHookedSounds.GetIndex(soundRegObj));
            buf.writeInt(entityId);
            buf.writeFloat(volume);
            buf.writeFloat(pitch);
        }
        else if (state.equals(SoundState.REMOVE)) {
            buf.writeUUID(uuid);
        }
    }
}

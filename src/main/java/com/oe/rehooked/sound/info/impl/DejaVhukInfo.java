package com.oe.rehooked.sound.info.impl;

import com.oe.rehooked.data.HookData;
import com.oe.rehooked.item.ReHookedItems;
import com.oe.rehooked.network.packets.client.CSoundPacket;
import com.oe.rehooked.network.packets.server.SSoundPacket;
import com.oe.rehooked.sound.info.def.IKeepAliveInfo;
import com.oe.rehooked.utils.HandlerHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.UUID;

public class DejaVhukInfo implements IKeepAliveInfo {
    public static final double SPEED_THRESHOLD = 0.4;
    // each tick of the server sound manager is 10 game ticks
    public static final long MAX_TICKS_BELOW = 9;
    
    protected int entityId;
    protected RegistryObject<SoundEvent> soundRegObj;
    protected float volume;
    protected float pitch;
    
    protected long ticksBelowThreshold = 0;
    
    public DejaVhukInfo() {}
    
    public DejaVhukInfo(int entityId) {
        this.entityId = entityId;
    }
    
    public DejaVhukInfo(int entityId, RegistryObject<SoundEvent> soundRegObj, float volume, float pitch) {
        this.entityId = entityId;
        this.soundRegObj = soundRegObj;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public DejaVhukInfo instantiate(SSoundPacket soundPacket) {
        this.entityId = soundPacket.entityId;
        this.soundRegObj = soundPacket.soundRegObj;
        this.volume = soundPacket.volume;
        this.pitch = soundPacket.pitch;
        return this;
    }

    @Override
    public boolean keepAlive(Level level) {
        if (!(level.getEntity(entityId) instanceof Player player && player.isAlive())) return false;
        Optional<Boolean> keepAlive = HandlerHelper.getHookHandler(player).map(handler -> {
            boolean hasDejaVhuk = handler.getHookData().map(HookData::type).map(type -> type.equals(ReHookedItems.DEJA_VU)).orElse(false);
            boolean aboveSpeedThreshold = handler.getMomentum() != null || 
                    (handler.shouldMoveThisTick() && handler.actualPlayerPositionChange().length() > SPEED_THRESHOLD);
            return hasDejaVhuk && aboveSpeedThreshold;
        });
        if (keepAlive.isPresent() && keepAlive.get()) ticksBelowThreshold = 0;
        else ticksBelowThreshold++;
        return ticksBelowThreshold < MAX_TICKS_BELOW;
    }

    @Override
    public CSoundPacket createSyncPacket(UUID uuid) {
        return CSoundPacket.createAddPacket(uuid, soundRegObj, entityId, volume, pitch);
    }

    @Override
    public String toString() {
        return "DejaVhukInfo[entityId: " + entityId + ", ticksBelowThreshold: " + ticksBelowThreshold + "]";
    }
}

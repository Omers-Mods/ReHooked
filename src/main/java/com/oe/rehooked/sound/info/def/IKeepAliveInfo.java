package com.oe.rehooked.sound.info.def;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.network.packets.client.CSoundPacket;
import com.oe.rehooked.network.packets.server.SSoundPacket;
import net.minecraft.world.level.Level;

import java.util.UUID;

public interface IKeepAliveInfo {
    <T extends IKeepAliveInfo> T instantiate(SSoundPacket soundPacket);
    boolean keepAlive(Level level);
    default void onKill() {
        ReHookedMod.LOGGER.debug("Killed sound: {}", this);
    }
    CSoundPacket createSyncPacket(UUID uuid);
}

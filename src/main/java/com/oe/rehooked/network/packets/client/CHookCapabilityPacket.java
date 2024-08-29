package com.oe.rehooked.network.packets.client;

import com.oe.rehooked.network.packets.common.HookCapabilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CHookCapabilityPacket extends HookCapabilityPacket {
    private final UUID hookUUID;

    public CHookCapabilityPacket(byte packetType, int additional, UUID hookUUID) {
        super(packetType, additional);
        this.hookUUID = hookUUID;
    }

    public CHookCapabilityPacket(byte packetType, UUID hookUUID) {
        super(packetType);
        this.hookUUID = hookUUID;
    }

    public CHookCapabilityPacket(FriendlyByteBuf buf) {
        super(buf);
        hookUUID = buf.readUUID();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        super.encode(buf);
        buf.writeUUID(hookUUID);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
    }
}

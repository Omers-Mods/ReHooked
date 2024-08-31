package com.oe.rehooked.network.packets.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class HookCapabilityPacket {
    protected final int packetType;
    protected final int additional;
    
    public HookCapabilityPacket(int packetType, int additional) {
        this.packetType = packetType;
        this.additional = additional;
    }
    
    public HookCapabilityPacket(int packetType) {this(packetType, 0);}
    
    public HookCapabilityPacket(FriendlyByteBuf buf) {
        this.packetType = buf.readInt();
        this.additional = buf.readInt();
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(packetType);
        buf.writeInt(additional);
    }
    
    public abstract void handle(Supplier<NetworkEvent.Context> context);
}

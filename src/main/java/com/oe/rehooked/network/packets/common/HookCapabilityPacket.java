package com.oe.rehooked.network.packets.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class HookCapabilityPacket {
    public static final byte SHOOT = 1;
    public static final byte RETRACT = 2;
    public static final byte ALL = 4;
    
    protected final byte packetType;
    protected final int additional;
    
    public HookCapabilityPacket(byte packetType, int additional) {
        this.packetType = packetType;
        this.additional = additional;
    }
    
    public HookCapabilityPacket(byte packetType) {this(packetType, 0);}
    
    public HookCapabilityPacket(FriendlyByteBuf buf) {
        this.packetType = buf.readByte();
        this.additional = buf.readInt();
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(packetType);
        buf.writeInt(additional);
    }
    
    public abstract void handle(Supplier<NetworkEvent.Context> context);
}

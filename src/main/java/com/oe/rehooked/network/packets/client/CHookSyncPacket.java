package com.oe.rehooked.network.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CHookSyncPacket {
    private final double x_deltaV;
    private final double y_deltaV;
    private final double z_deltaV;
    private final double x_deltaR;
    private final double y_deltaR;

    public CHookSyncPacket(double xDeltaV, double yDeltaV, double zDeltaV, double xDeltaR, double yDeltaR) {
        x_deltaV = xDeltaV;
        y_deltaV = yDeltaV;
        z_deltaV = zDeltaV;
        x_deltaR = xDeltaR;
        y_deltaR = yDeltaR;
    }
    
    public CHookSyncPacket(FriendlyByteBuf buf) {
        x_deltaV = buf.readDouble();
        y_deltaV = buf.readDouble();
        z_deltaV = buf.readDouble();
        x_deltaR = buf.readDouble();
        y_deltaR = buf.readDouble();
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(x_deltaV);
        buf.writeDouble(y_deltaV);
        buf.writeDouble(z_deltaV);
        buf.writeDouble(x_deltaR);
        buf.writeDouble(y_deltaR);
    }
    
    public void handle(Supplier<NetworkEvent.Context> context) {
        
    }
}

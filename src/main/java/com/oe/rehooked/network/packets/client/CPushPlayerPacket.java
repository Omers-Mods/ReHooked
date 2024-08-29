package com.oe.rehooked.network.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CPushPlayerPacket {
    private final Vec3 pushPower;
    
    public CPushPlayerPacket(Vec3 pushPower) {
        this.pushPower = pushPower;
    }
    
    public CPushPlayerPacket(FriendlyByteBuf buffer) {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        this.pushPower = new Vec3(x, y, z);
    }
    
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeDouble(pushPower.x);
        buffer.writeDouble(pushPower.y);
        buffer.writeDouble(pushPower.z);
    }
    
    public void handle(Supplier<NetworkEvent.Context> context) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
            Player player = Minecraft.getInstance().player;
            if (player != null)
                player.setDeltaMovement(pushPower);
        });
        context.get().setPacketHandled(true);
    }
}

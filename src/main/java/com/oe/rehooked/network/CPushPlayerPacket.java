package com.oe.rehooked.network;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CPushPlayerPacket {
    private Vec3 pushPower;
    
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
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ReHookedMod.LOGGER.debug("Adding deltaV to player {} - {}", pushPower, player.getDisplayName());
            player.addDeltaMovement(pushPower);
        }
    }
}

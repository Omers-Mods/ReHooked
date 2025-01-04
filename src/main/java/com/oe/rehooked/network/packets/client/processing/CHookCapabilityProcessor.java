package com.oe.rehooked.network.packets.client.processing;

import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.network.handlers.IHandler;
import com.oe.rehooked.network.packets.client.CHookCapabilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class CHookCapabilityProcessor implements IHandler {

    public static void handle(CHookCapabilityPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handle(packet)));
        context.get().setPacketHandled(true);
    }

    private static void handle(CHookCapabilityPacket packet) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Optional<IClientPlayerHookHandler> optHandler = IClientPlayerHookHandler.FromPlayer(player).resolve();
        if (optHandler.isPresent()) {
            IClientPlayerHookHandler handler = optHandler.get();
            switch (packet.packetType) {
                case ADD_HOOK -> handler.addHook(packet.id);
                case RETRACT_HOOK -> handler.removeHook(packet.id);
                case RETRACT_ALL_HOOKS -> handler.removeAllHooks();
                case FORCE_UPDATE -> handler.update();
            }
        }
    }
}

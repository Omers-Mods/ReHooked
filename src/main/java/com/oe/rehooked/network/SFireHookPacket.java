package com.oe.rehooked.network;

import com.oe.rehooked.client.KeyBindings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class SFireHookPacket {
    public SFireHookPacket() {}
    
    public SFireHookPacket(FriendlyByteBuf buffer) {}

    public void encode(FriendlyByteBuf buffer) {}

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null) return;
        // validate that the player has a hook in curio slot
        CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(curiosInventory -> curiosInventory.getStacksHandler("hook")).ifPresent(hook -> {
                    // todo: fire a hook and update the player information
                    // todo: send packet to all clients, informing them of the new hook
                });
    }
}

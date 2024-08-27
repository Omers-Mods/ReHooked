package com.oe.rehooked.network;

import com.oe.rehooked.capabilities.hooks.PlayerHookCapabilityProvider;
import com.oe.rehooked.item.hook.HookItem;
import net.minecraft.network.FriendlyByteBuf;
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
        player.getCapability(PlayerHookCapabilityProvider.PLAYER_HOOK_HANDLER).ifPresent(handler -> {
            handler.owner(player);
            CuriosApi.getCuriosInventory(player)
                    .ifPresent(inventory -> inventory.findFirstCurio(itemStack -> itemStack.getItem() instanceof HookItem)
                            .ifPresent(hook -> handler.hookType(((HookItem) hook.stack().getItem()).getHookType())
                                    .shootHook()));
        });
    }
}

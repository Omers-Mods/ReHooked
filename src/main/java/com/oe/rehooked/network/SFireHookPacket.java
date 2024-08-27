package com.oe.rehooked.network;

import com.oe.rehooked.capabilities.hooks.IPlayerHookHandler;
import com.oe.rehooked.capabilities.hooks.PlayerHookCapabilityProvider;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.item.hook.HookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Handler;

public class SFireHookPacket {
    public SFireHookPacket() {}
    
    public SFireHookPacket(FriendlyByteBuf buffer) {}

    public void encode(FriendlyByteBuf buffer) {}

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null) return;
        // todo: remove debug message
        player.sendSystemMessage(Component.literal("Received Fire Command"));
        player.getCapability(PlayerHookCapabilityProvider.PLAYER_HOOK_HANDLER).ifPresent(handler -> {
            handler.owner(player);
            CuriosApi.getCuriosInventory(player)
                    .ifPresent(inventory -> inventory.findFirstCurio(itemStack -> itemStack.getItem() instanceof HookItem)
                            .ifPresent(hook -> handler.hookType(((HookItem) hook.stack().getItem()).getHookType())
                                    .shootHook()));
        });
    }
}

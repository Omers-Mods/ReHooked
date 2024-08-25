package com.oe.rehooked.network;

import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.item.hook.HookItem;
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
        // todo: remove debug message
        player.sendSystemMessage(Component.literal("Received Fire Command"));
        // validate that the player has a hook in curio slot
        CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(curiosInventory -> curiosInventory.findFirstCurio(itemStack -> itemStack.getItem() instanceof HookItem))
                        .ifPresent(slotResult -> {
                            HookEntity hookEntity = new HookEntity(player.level(), player);
                            player.level().addFreshEntity(hookEntity);
                            hookEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.0f, 0.0f);
                            // todo: remove debug message
                            player.sendSystemMessage(Component.literal("Fired hook!"));
                        });
    }
}

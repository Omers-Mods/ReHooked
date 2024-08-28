package com.oe.rehooked.events.client;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.client.KeyBindings;
import com.oe.rehooked.events.custom.PlayerPushEvent;
import com.oe.rehooked.network.PacketHandler;
import com.oe.rehooked.network.SHookCapabilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (KeyBindings.FIRE_HOOK_KEY.consumeClick() && player != null) {
            player.sendSystemMessage(Component.literal("Player pressed shoot key"));
            CuriosApi.getCuriosInventory(player).resolve()
                    .flatMap(curiosInventory -> curiosInventory.getStacksHandler("hook"))
                    .ifPresent(hook -> PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.SHOOT)));
        }
        if (KeyBindings.REMOVE_ALL_HOOKS_KEY.consumeClick() && player != null) {
            player.sendSystemMessage(Component.literal("Player pressed remove all hooks key"));
            CuriosApi.getCuriosInventory(player).resolve()
                    .flatMap(curiosInventory -> curiosInventory.getStacksHandler("hook"))
                    .ifPresent(hook -> PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.ALL)));
        }
    }
    
    @SubscribeEvent
    public static void onPlayerPush(PlayerPushEvent event) {
        event.getEntity().addDeltaMovement(event.getPushPower());
    }
}

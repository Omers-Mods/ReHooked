package com.oe.rehooked.events.subscribers.client;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.client.KeyBindings;
import com.oe.rehooked.events.definition.player.PlayerPushEvent;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.server.SHookCapabilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
    private static long ticksSinceShot = 0;
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        ticksSinceShot++;
        LocalPlayer player = Minecraft.getInstance().player;
        if (KeyBindings.FIRE_HOOK_KEY.consumeClick() && player != null && ticksSinceShot > 5) {
            ticksSinceShot = 0;
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

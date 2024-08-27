package com.oe.rehooked.client.handler;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.client.KeyBindings;
import com.oe.rehooked.network.PacketHandler;
import com.oe.rehooked.network.SFireHookPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        // if fire key was pressed and player exists
        if (KeyBindings.FIRE_HOOK_KEY.consumeClick() && player != null) {
            // if player has curios inventory
            // if player has hook in curios slot
            CuriosApi.getCuriosInventory(player).resolve()
                    .flatMap(curiosInventory -> curiosInventory.getStacksHandler("hook"))
                    .ifPresent(hook -> PacketHandler.sendToServer(new SFireHookPacket()));
        }
    }
}

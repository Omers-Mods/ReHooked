package com.oe.rehooked.events.subscribers.client;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.capabilities.hooks.IPlayerHookHandler;
import com.oe.rehooked.client.KeyBindings;
import com.oe.rehooked.events.definition.player.PlayerPushEvent;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.server.SHookCapabilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static long ticksSinceShot = 0;
    private static Vec3 hookMovement = null;
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (event.phase == TickEvent.Phase.START) {
            hookMovement = null;
        }
        else if (hookMovement != null) {
            player.resetPos();
            if (hookMovement.length() > 0.5)
                Minecraft.getInstance().player.setDeltaMovement(hookMovement);
        }
        ticksSinceShot++;
        if (KeyBindings.FIRE_HOOK_KEY.consumeClick() && ticksSinceShot > 5) {
            ticksSinceShot = 0;
            LOGGER.debug("Player pressed shoot key");
            CuriosApi.getCuriosInventory(player).resolve()
                    .flatMap(curiosInventory -> curiosInventory.getStacksHandler("hook"))
                    .ifPresent(hook -> PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.SHOOT)));
        }
        if (KeyBindings.REMOVE_ALL_HOOKS_KEY.consumeClick()) {
            LOGGER.debug("Player pressed remove all hooks key");
            CuriosApi.getCuriosInventory(player).resolve()
                    .flatMap(curiosInventory -> curiosInventory.getStacksHandler("hook"))
                    .ifPresent(hook -> {
                        PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.ALL));
                        IPlayerHookHandler.FromPlayer(player).ifPresent(IPlayerHookHandler::removeAllHooks);
                    });
        }
    }
    
    @SubscribeEvent
    public static void onPlayerPush(PlayerPushEvent event) {
        hookMovement = event.getPushPower();
    }
}

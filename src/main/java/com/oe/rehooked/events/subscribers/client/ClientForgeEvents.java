package com.oe.rehooked.events.subscribers.client;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.client.KeyBindings;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.server.SHookCapabilityPacket;
import com.oe.rehooked.utils.CurioUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static long ticksSinceShot = 0;
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ticksSinceShot++;
        if (KeyBindings.FIRE_HOOK_KEY.consumeClick() && ticksSinceShot > 5) {
            ticksSinceShot = 0;
            LOGGER.debug("Player pressed shoot key");
            CurioUtils.GetCuriosOfType(HookItem.class, player).flatMap(CurioUtils::GetIfUnique).ifPresent(hookItem -> {
                Entity camera = Minecraft.getInstance().getCameraEntity();
                ICommonPlayerHookHandler.FromPlayer(player)
                        .ifPresent(handler -> handler.shootFromRotation(camera.getXRot(), camera.getYRot()));
            });
        }
        if (KeyBindings.REMOVE_ALL_HOOKS_KEY.consumeClick()) {
            LOGGER.debug("Player pressed remove all hooks key");
            CurioUtils.GetCuriosOfType(HookItem.class, player).flatMap(CurioUtils::GetIfUnique).ifPresent(hookItem -> {
                PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.State.RETRACT_ALL_HOOKS));
                ICommonPlayerHookHandler.FromPlayer(player).ifPresent(ICommonPlayerHookHandler::removeAllHooks);
            });
        }
    }
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player == null) return;
        if (event.phase.equals(TickEvent.Phase.END)) {
            ICommonPlayerHookHandler.FromPlayer(player).ifPresent(handler -> {
                handler.update();
                if (handler.shouldMoveThisTick()) {
                    player.setDeltaMovement(handler.getDeltaVThisTick());
                }
            });
        }
    }
}

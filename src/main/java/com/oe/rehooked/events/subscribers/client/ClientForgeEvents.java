package com.oe.rehooked.events.subscribers.client;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.client.KeyBindings;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.handlers.hook.client.CPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.mixin.common.player.IReHookedPlayerExtension;
import com.oe.rehooked.utils.CurioUtils;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.slf4j.Logger;

import java.util.Optional;

@EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientForgeEvents {
    private static long ticksSinceShot = 0;
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ticksSinceShot++;
        Optional<IClientPlayerHookHandler> optHandler = IClientPlayerHookHandler.fromPlayer(player);
        if (optHandler.isEmpty()) {
            ((IReHookedPlayerExtension) player).reHooked$setHookHandler(new CPlayerHookHandler().setOwner(player));
            return;
        }
        IClientPlayerHookHandler handler = optHandler.get();
        if (KeyBindings.FIRE_HOOK_KEY.consumeClick() && ticksSinceShot > 5) {
            ticksSinceShot = 0;
            CurioUtils.GetCuriosOfType(HookItem.class, player).flatMap(CurioUtils::GetIfUnique).ifPresent(hookStack -> {
                Entity camera = Minecraft.getInstance().getCameraEntity();
                handler.shootFromRotation(camera.getXRot(), camera.getYRot());
            });
        }
        if (KeyBindings.RETRACT_HOOK_KEY.consumeClick() && !handler.getHooks().isEmpty()) {
            Optional<HookEntity> target = VectorHelper.acquireLookTarget(HookEntity.class, player, 0.5);
            target.ifPresent(handler::removeHook);
        }
        
        handler.setOwner(player).update();
        if (handler.shouldMoveThisTick()) {
            player.setDeltaMovement(handler.getDeltaVThisTick());
        }
        if (KeyBindings.REMOVE_ALL_HOOKS_KEY.consumeClick() && !handler.getHooks().isEmpty() && !handler.getHookData().map(HookData::isCreative).orElse(true)) {
            handler.jump();
        }
        handler.storeLastPlayerPosition();
    }
}

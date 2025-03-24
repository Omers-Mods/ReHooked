package com.oe.rehooked.events.subscribers.common;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.extensions.player.IReHookedPlayerExtension;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import com.oe.rehooked.handlers.hook.server.SPlayerHookHandler;
import com.oe.rehooked.utils.HandlerHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

@EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ForgeEventBus {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        LOGGER.debug("Instantiating server player hook handler...");
        Player player = event.getEntity();
        ((IReHookedPlayerExtension) player).reHooked$setHookHandler(new SPlayerHookHandler().setOwner(player));
    }
    
    @SubscribeEvent
    public static void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        LOGGER.debug("Cleaning up server player hook handler...");
        IServerPlayerHookHandler.fromPlayer(event.getEntity()).ifPresent(IServerPlayerHookHandler::removeAllHooks);
    }
    
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        event.getServer().getPlayerList().getPlayers().forEach(player -> 
                IServerPlayerHookHandler.fromPlayer(player).ifPresentOrElse(handler -> {
                    handler.setOwner(player).update();
                    if (handler.shouldMoveThisTick()) {
                        player.setDeltaMovement(handler.getDeltaVThisTick());
                    }
                    handler.storeLastPlayerPosition();
                }, () -> ((IReHookedPlayerExtension) player)
                        .reHooked$setHookHandler(new SPlayerHookHandler().setOwner(player)))
        );
    }
    
    @SubscribeEvent
    public static void onBreakEvent(PlayerEvent.BreakSpeed event) {
        var player = event.getEntity();
        HandlerHelper.getHookHandler(player).ifPresent(handler -> {
            // negate the in-air/water mining speed debuff
            if (handler.countPulling() > 0) {
                float mult = 1;
                if (player.isEyeInFluid(FluidTags.WATER)) {
                    mult /= (float) player.getAttribute(Attributes.SUBMERGED_MINING_SPEED).getValue();
                }
                if (!player.onGround()) {
                    mult *= 5;
                }
                event.setNewSpeed(event.getNewSpeed() * mult);
            }
        });
    }
    
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.getEntity().level().isClientSide()) return;
        IServerPlayerHookHandler.fromPlayer(event.getOriginal()).ifPresent(handler -> {
            handler.removeAllHooks();
            handler.copyFrom(handler).setOwner(event.getEntity()).afterDeath();
            ((IReHookedPlayerExtension) event.getEntity()).reHooked$setHookHandler(handler);
        });
    }
    
    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        HandlerHelper.getHookHandler(event.getEntity()).ifPresent(handler -> {
            if (!event.getEntity().level().isClientSide()) {
                handler.getHooks().forEach(HookEntity::discard);
            }
            handler.getHooks().clear();
            handler.afterDeath();
        });
    }
}

package com.oe.rehooked.events.subscribers.common;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.capabilities.hooks.ClientHookCapabilityProvider;
import com.oe.rehooked.capabilities.hooks.ServerHookCapabilityProvider;
import com.oe.rehooked.capabilities.meme.ServerDejaVuCapabilityProvider;
import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import com.oe.rehooked.utils.HandlerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBus {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (player.level().isClientSide()) {
                if (!IClientPlayerHookHandler.FromPlayer(player).isPresent()) {
                    event.addCapability(new ResourceLocation(ReHookedMod.MOD_ID, "capabilities.hook.client"),
                            new ClientHookCapabilityProvider());
                }
            }
            else {
                if (!player.level().isClientSide()) {
                    if (!IServerPlayerHookHandler.FromPlayer(player).isPresent()) {
                        event.addCapability(new ResourceLocation(ReHookedMod.MOD_ID, "capabilities.hook.server"),
                                new ServerHookCapabilityProvider());
                    }
                    if (!player.getCapability(ServerDejaVuCapabilityProvider.SERVER_DEJA_VU_HANDLER).isPresent()) {
                        event.addCapability(new ResourceLocation(ReHookedMod.MOD_ID, "capabilities.deja_vu.server"),
                                new ServerDejaVuCapabilityProvider());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        IServerPlayerHookHandler.FromPlayer(event.getEntity()).ifPresent(IServerPlayerHookHandler::removeAllHooks);
    }
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;
        event.getServer().getPlayerList().getPlayers().forEach(player -> {
            IServerPlayerHookHandler.FromPlayer(player).ifPresent(handler -> {
                handler.setOwner(player).update();
                if (handler.shouldMoveThisTick()) {
                    player.setDeltaMovement(handler.getDeltaVThisTick());
                }
                handler.storeLastPlayerPosition();
            });
            player.getCapability(ServerDejaVuCapabilityProvider.SERVER_DEJA_VU_HANDLER).ifPresent(handler -> {
                handler.setOwner(player).tick();
            });
        });
    }
    
    @SubscribeEvent
    public static void onBreakEvent(PlayerEvent.BreakSpeed event) {
        HandlerHelper.getHookHandler(event.getEntity()).ifPresent(handler -> {
            // negate the in-air mining speed debuff
            if (handler.countPulling() > 0) event.setNewSpeed(event.getNewSpeed() * 5);
        });
    }
    
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.getEntity().level().isClientSide()) return;
        HandlerHelper.getHookHandler(event.getEntity()).ifPresent(newHandler -> {
            HandlerHelper.getHookHandler(event.getOriginal()).ifPresent(oldHandler -> {
                if (newHandler instanceof IServerPlayerHookHandler newServerHandler && 
                        oldHandler instanceof IServerPlayerHookHandler oldServerHandler) {
                    newServerHandler.copyFrom(oldServerHandler);
                    newHandler.setOwner(event.getEntity());
                    newHandler.update();
                }
            });
        });
    }
}

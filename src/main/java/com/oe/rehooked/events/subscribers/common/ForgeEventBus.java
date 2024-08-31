package com.oe.rehooked.events.subscribers.common;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.capabilities.hooks.ClientHookCapabilityProvider;
import com.oe.rehooked.capabilities.hooks.ServerHookCapabilityProvider;
import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBus {
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
                if (!player.level().isClientSide() && !IServerPlayerHookHandler.FromPlayer(player).isPresent()) {
                    event.addCapability(new ResourceLocation(ReHookedMod.MOD_ID, "capabilities.hook.server"),
                            new ServerHookCapabilityProvider());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        IServerPlayerHookHandler.FromPlayer(event.getEntity()).ifPresent(IServerPlayerHookHandler::removeAllHooks);
    }
}

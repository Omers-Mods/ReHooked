package com.oe.rehooked.events.subscribers.common;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.capabilities.hooks.PlayerHookCapabilityProvider;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
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
        if (event.getObject().level().isClientSide()) return;
        if (event.getObject() instanceof Player player) {
            if (!ICommonPlayerHookHandler.FromPlayer(player).isPresent()) {
                event.addCapability(new ResourceLocation(ReHookedMod.MOD_ID, "properties"),
                        new PlayerHookCapabilityProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        ICommonPlayerHookHandler.FromPlayer(event.getEntity()).ifPresent(ICommonPlayerHookHandler::removeAllHooks);
    }
}

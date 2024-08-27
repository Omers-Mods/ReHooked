package com.oe.rehooked.events;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.capabilities.hooks.IPlayerHookHandler;
import com.oe.rehooked.capabilities.hooks.PlayerHookCapabilityProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBus {
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        // revive old capabilities
        event.getOriginal().reviveCaps();
        // get the provider
        LazyOptional<IPlayerHookHandler> hookCap = event.getOriginal().getCapability(PlayerHookCapabilityProvider.PLAYER_HOOK_HANDLER);
        // remove all old hooks and hook type
        hookCap.ifPresent(handler -> {
            handler.removeAllHooks();
            handler.hookType("");
        });
        // in case of death copy the old data to the new capability
        if (event.isWasDeath()) {
            hookCap.ifPresent(oldStore ->
                    event.getEntity().getCapability(PlayerHookCapabilityProvider.PLAYER_HOOK_HANDLER)
                            .ifPresent(newStore -> newStore.copyFrom(oldStore)));
        }
        // invalidate old capabilities
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerHookCapabilityProvider.PLAYER_HOOK_HANDLER).isPresent()) {
                event.addCapability(new ResourceLocation(ReHookedMod.MOD_ID, "properties"),
                        new PlayerHookCapabilityProvider());
            }
        }
    }
}

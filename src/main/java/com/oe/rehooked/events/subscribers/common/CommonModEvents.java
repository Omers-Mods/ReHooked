package com.oe.rehooked.events.subscribers.common;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.item.ReHookedItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CommonModEvents {
    @SubscribeEvent
    public static void onLoadConfig(ModConfigEvent.Loading event) {
        if (event.getConfig().getType().equals(ModConfig.Type.SERVER))
            loadConfig();
    }
    
    @SubscribeEvent
    public static void onReloadConfig(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType().equals(ModConfig.Type.SERVER))
            loadConfig();
    }
    
    private static void loadConfig() {
        ReHookedItems.RegisterConfigProperties();
    }
}

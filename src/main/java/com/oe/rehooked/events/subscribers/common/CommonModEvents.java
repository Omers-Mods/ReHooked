package com.oe.rehooked.events.subscribers.common;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.item.ReHookedItems;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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

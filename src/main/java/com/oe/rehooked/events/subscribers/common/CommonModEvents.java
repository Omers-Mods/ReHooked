package com.oe.rehooked.events.subscribers.common;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.item.ReHookedItems;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {
    @SubscribeEvent
    public static void onLoadConfig(ModConfigEvent.Loading event) {
        loadConfig();
    }
    
    @SubscribeEvent
    public static void onReloadConfig(ModConfigEvent.Reloading event) {
        loadConfig();
    }
    
    private static void loadConfig() {
        ReHookedMod.LOGGER.info("Server config loaded, registering item properties.");
        ReHookedItems.RegisterConfigProperties();
        ReHookedMod.LOGGER.info("Finished registering item properties from config.");
    }
}

package com.oe.rehooked;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.block.ReHookedBlocks;
import com.oe.rehooked.config.ReHookedConfig;
import com.oe.rehooked.entities.ReHookedEntities;
import com.oe.rehooked.item.ReHookedComponents;
import com.oe.rehooked.item.ReHookedItems;
import com.oe.rehooked.particle.ReHookedParticles;
import com.oe.rehooked.sound.ReHookedSounds;
import com.oe.rehooked.tabs.ReHookedCreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ReHookedMod.MOD_ID)
public class ReHookedMod {
    public static final String MOD_ID = "rehooked";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ReHookedMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("ReHooked started initializing...");

        // register config
        ReHookedConfig.init(modContainer);
        // register mod sounds
        ReHookedSounds.init(modEventBus);
        // register mod particles
        ReHookedParticles.init(modEventBus);
        // Register mod entities
        ReHookedEntities.init(modEventBus);
        // Register mod creative tab
        ReHookedCreativeModeTab.init(modEventBus);
        // Register mod items
        ReHookedItems.init(modEventBus);
        // Register mod blocks
        ReHookedBlocks.init(modEventBus);
        // Register mod crafting components
        ReHookedComponents.init(modEventBus);
        
        LOGGER.info("ReHooked finished registration.");
    }
}

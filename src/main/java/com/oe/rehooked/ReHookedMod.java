package com.oe.rehooked;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.entities.ReHookedEntities;
import com.oe.rehooked.entities.hook.HookEntityRenderer;
import com.oe.rehooked.item.ReHookedItemProperties;
import com.oe.rehooked.item.ReHookedItems;
import com.oe.rehooked.tabs.ReHookedCreativeModeTab;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ReHookedMod.MOD_ID)
public class ReHookedMod {
    public static final String MOD_ID = "rehooked";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ReHookedMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register mod entities
        ReHookedEntities.register(modEventBus);
        // Register mod creative tab
        ReHookedCreativeModeTab.register(modEventBus);
        // Register mod items
        ReHookedItems.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ReHookedItemProperties.addCustomItemProperties();
            });
            
            EntityRenderers.register(ReHookedEntities.HOOK_PROJECTILE.get(), HookEntityRenderer::new);
        }
    }
}

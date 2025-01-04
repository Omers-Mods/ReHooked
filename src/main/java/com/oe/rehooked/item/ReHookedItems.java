package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.config.client.visuals.HookVisualsConfig;
import com.oe.rehooked.config.server.stats.HookStatsConfig;
import com.oe.rehooked.data.AdditionalHandlersRegistry;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.handlers.additional.FireHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.particle.ReHookedParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ReHookedItems {
    public static final String WOOD = "wood";
    public static final String IRON = "iron";
    public static final String DIAMOND = "diamond";
    public static final String RED = "red";
    public static final String ENDER = "ender";
    public static final String BLAZE = "blaze";
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<Item> WOOD_HOOK = CreateHookItem(WOOD);
    public static final RegistryObject<Item> IRON_HOOK = CreateHookItem(IRON);
    public static final RegistryObject<Item> DIAMOND_HOOK = CreateHookItem(DIAMOND);
    public static final RegistryObject<Item> RED_HOOK = CreateHookItem(RED);
    public static final RegistryObject<Item> BLAZE_HOOK = CreateHookItem(BLAZE);
    public static final RegistryObject<Item> ENDER_HOOK = CreateHookItem(ENDER);
    
    private static RegistryObject<Item> CreateHookItem(String type) {
        return ITEMS.register(type + "_hook", () -> new HookItem(type));
    }
    
    public static void init(IEventBus eventBus) {
        // register the objects
        ITEMS.register(eventBus);
    }
    
    private static HookData completeConfigData(String type, Supplier<ParticleOptions> particleType, int minParticlesPerBlock, int maxParticlesPerBlock, double radius, int ticksBetweenSpawns) {
        return HookStatsConfig
                .getConfigDataForType(type)
                .map(HookStatsConfig.HookConfigData::getData)
                .map(partial -> partial.complete(type, getHookTexture(type), particleType, minParticlesPerBlock, maxParticlesPerBlock, radius, ticksBetweenSpawns))
                .orElse(null);
    }
    
    private static HookData completeConfigData(String type) {
        return HookStatsConfig
                .getConfigDataForType(type)
                .map(HookStatsConfig.HookConfigData::getData)
                .map(partial -> partial.complete(type, getHookTexture(type), () -> null, 0, 0, 0, 0))
                .orElse(null);
    }
    
    private static ResourceLocation getHookTexture(String name) {
        return new ResourceLocation(ReHookedMod.MOD_ID, "textures/entity/hook/" + name + "/" + name + ".png");
    }
    
    private static void registerHookWithParticles(String type, Supplier<ParticleOptions> particleType, int minParticlesPerBlock, int maxParticlesPerBlock, double radius, int ticksBetweenSpawns) {
        HookRegistry.registerHook(type, completeConfigData(type, particleType, minParticlesPerBlock, maxParticlesPerBlock, radius, ticksBetweenSpawns));
    }
    private static void registerHookWithChain(String type) {
        HookRegistry.registerHook(type, completeConfigData(type));
    }
    
    public static void registerConfigProperties() {
        // define all hook variants
        registerHookWithChain(WOOD);
        
        registerHookWithChain(IRON);
        
        registerHookWithChain(DIAMOND);
        
        if (HookVisualsConfig.getChainSetting(ENDER).map(value -> value.get().equals(HookVisualsConfig.CHAIN)).orElse(false))
            registerHookWithChain(ENDER);
        else
            registerHookWithParticles(ENDER, ReHookedParticles.ENDER_HOOK_PARTICLE::get, 1, 2, 0.2, 4);
        
        registerHookWithParticles(RED, ReHookedParticles.RED_HOOK_PARTICLE::get, 1, 2, 0.1, 4);

        if (HookVisualsConfig.getChainSetting(BLAZE).map(value -> value.get().equals(HookVisualsConfig.CHAIN)).orElse(false))
            registerHookWithChain(BLAZE);
        else
            registerHookWithParticles(BLAZE, ParticleTypes.FLAME::getType, 0, 1, 0.1, 20);
        AdditionalHandlersRegistry.registerHandler(BLAZE, FireHookHandler.class);
    }
}

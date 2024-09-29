package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.config.server.stats.HookStatsConfig;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
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
    public static final String DEJA_VU = "deja_vu";
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<Item> WOOD_HOOK = CreateHookItem(WOOD);
    public static final RegistryObject<Item> IRON_HOOK = CreateHookItem(IRON);
    public static final RegistryObject<Item> DIAMOND_HOOK = CreateHookItem(DIAMOND);
    public static final RegistryObject<Item> RED_HOOK = CreateHookItem(RED);
    public static final RegistryObject<Item> ENDER_HOOK = CreateHookItem(ENDER);
    public static final RegistryObject<Item> DEJA_VHUK = CreateHookItem(DEJA_VU);
    
    private static RegistryObject<Item> CreateHookItem(String type) {
        return ITEMS.register(type + "_hook", () -> new HookItem(type));
    }
    
    public static void Init(IEventBus eventBus) {
        // register the objects
        ITEMS.register(eventBus);
    }
    
    private static HookData CompleteConfigData(String type, Supplier<ParticleOptions> particleType, int minParticlesPerBlock, int maxParticlesPerBlock, double radius, int ticksBetweenSpawns) {
        return HookStatsConfig
                .GetConfigDataForType(type)
                .map(HookStatsConfig.HookConfigData::getData)
                .map(partial -> partial.complete(type, getHookTexture(type), particleType, minParticlesPerBlock, maxParticlesPerBlock, radius, ticksBetweenSpawns))
                .orElse(null);
    }
    
    private static HookData CompleteConfigData(String type) {
        return HookStatsConfig
                .GetConfigDataForType(type)
                .map(HookStatsConfig.HookConfigData::getData)
                .map(partial -> partial.complete(type, getHookTexture(type), () -> null, 0, 0, 0, 0))
                .orElse(null);
    }
    
    private static ResourceLocation getHookTexture(String name) {
        return new ResourceLocation(ReHookedMod.MOD_ID, "textures/entity/hook/" + name + "/" + name + ".png");
    }
    
    private static void RegisterHookWithParticles(String type, Supplier<ParticleOptions> particleType, int minParticlesPerBlock, int maxParticlesPerBlock, double radius, int ticksBetweenSpawns) {
        HookRegistry.registerHook(type, CompleteConfigData(type, particleType, minParticlesPerBlock, maxParticlesPerBlock, radius, ticksBetweenSpawns));
    }
    private static void RegisterHookWithChain(String type) {
        HookRegistry.registerHook(type, CompleteConfigData(type));
    }
    
    public static void RegisterConfigProperties() {
        // define all hook variants
        RegisterHookWithChain(WOOD);
        RegisterHookWithChain(IRON);
        RegisterHookWithChain(DIAMOND);
        RegisterHookWithParticles(ENDER, ReHookedParticles.ENDER_HOOK_PARTICLE::get, 2, 3, 0.2, 4);
        RegisterHookWithParticles(RED, ReHookedParticles.RED_HOOK_PARTICLE::get, 2, 3, 0.1, 4);
        RegisterHookWithParticles(DEJA_VU, ParticleTypes.NOTE::getType, 0, 1, 0.5, 20);
    }
}

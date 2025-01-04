package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.data.AdditionalHandlersRegistry;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.handlers.additional.FireHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.particle.ReHookedParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ReHookedItems {
    public static final String WOOD = "wood";
    public static final String IRON = "iron";
    public static final String DIAMOND = "diamond";
    public static final String RED = "red";
    public static final String ENDER = "ender";
    public static final String BLAZE = "blaze";
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ReHookedMod.MOD_ID);
    
    public static final DeferredHolder<Item, HookItem> WOOD_HOOK = createHookItem(WOOD);
    public static final DeferredHolder<Item, HookItem> IRON_HOOK = createHookItem(IRON);
    public static final DeferredHolder<Item, HookItem> DIAMOND_HOOK = createHookItem(DIAMOND);
    public static final DeferredHolder<Item, HookItem> RED_HOOK = createHookItem(RED);
    public static final DeferredHolder<Item, HookItem> BLAZE_HOOK = createHookItem(BLAZE);
    public static final DeferredHolder<Item, HookItem> ENDER_HOOK = createHookItem(ENDER);
    
    private static DeferredHolder<Item, HookItem> createHookItem(String type) {
        return ITEMS.register(type + "_hook", () -> new HookItem(type));
    }
    
    public static void init(IEventBus eventBus) {
        // register the objects
        ITEMS.register(eventBus);
    }
    
    private static void setHookParticles(String type, Supplier<ParticleOptions> particleType, int minParticlesPerBlock, int maxParticlesPerBlock, double radius, int ticksBetweenSpawns) {
        HookRegistry.getHookData(type).ifPresent(data -> data
                .setParticleType(particleType)
                .setMinParticlesPerBlock(minParticlesPerBlock)
                .setMaxParticlesPerBlock(maxParticlesPerBlock)
                .setRadius(radius)
                .setTicksBetweenSpawns(ticksBetweenSpawns));
    }
    
    public static void registerConfigProperties() {
        setHookParticles(ENDER, ReHookedParticles.ENDER_HOOK_PARTICLE::get, 1, 2, 0.2, 4);
        
        setHookParticles(RED, ReHookedParticles.RED_HOOK_PARTICLE::get, 1, 2, 0.1, 4);
        
        setHookParticles(BLAZE, ParticleTypes.FLAME::getType, 0, 1, 0.1, 20);
        AdditionalHandlersRegistry.registerHandler(BLAZE, FireHookHandler.class);
    }
}

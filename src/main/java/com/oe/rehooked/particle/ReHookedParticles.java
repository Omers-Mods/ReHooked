package com.oe.rehooked.particle;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReHookedParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = 
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ReHookedMod.MOD_ID);
    
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RED_HOOK_PARTICLE = 
            PARTICLE_TYPES.register("red_hook_particle", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ENDER_HOOK_PARTICLE = 
            PARTICLE_TYPES.register("ender_hook_particle", () -> new SimpleParticleType(true));
    
    public static void init(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}

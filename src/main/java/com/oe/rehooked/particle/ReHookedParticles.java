package com.oe.rehooked.particle;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ReHookedParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = 
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<SimpleParticleType> RED_HOOK_PARTICLE = 
            PARTICLE_TYPES.register("red_hook_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> ENDER_HOOK_PARTICLE = 
            PARTICLE_TYPES.register("ender_hook_particle", () -> new SimpleParticleType(true));
    
    public static void Init(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}

package com.oe.rehooked.data;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public record HookData(String type, int count, float range, float speed, float pullSpeed, boolean isCreative, 
                       ResourceLocation texture, 
                       Supplier<ParticleOptions> particleType, int minParticlesPerBlock, int maxParticlesPerBlock, double radius, int ticksBetweenSpawns) {
}

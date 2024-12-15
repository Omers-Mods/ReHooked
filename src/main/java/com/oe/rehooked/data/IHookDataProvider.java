package com.oe.rehooked.data;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface IHookDataProvider {
    String type();
    int count();
    float range();
    float speed();
    float pullSpeed();
    boolean isCreative();
    default ResourceLocation texture() {
        return ResourceLocation.fromNamespaceAndPath(ReHookedMod.MOD_ID, "textures/entity/hook/" + type() + "/" + type() + ".png");
    }
    boolean useParticles();
    void setUseParticles(Supplier<Boolean> useParticles);
    Supplier<ParticleOptions> particleType();
    IHookDataProvider setParticleType(Supplier<ParticleOptions> particleType);
    int minParticlesPerBlock();
    IHookDataProvider setMinParticlesPerBlock(int minParticlesPerBlock);
    int maxParticlesPerBlock();
    IHookDataProvider setMaxParticlesPerBlock(int maxParticlesPerBlock);
    double radius();
    IHookDataProvider setRadius(double radius);
    int ticksBetweenSpawns();
    IHookDataProvider setTicksBetweenSpawns(int ticksBetweenSpawns);
}

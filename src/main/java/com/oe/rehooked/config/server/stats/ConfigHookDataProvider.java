package com.oe.rehooked.config.server.stats;

import com.oe.rehooked.data.IHookDataProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

public class ConfigHookDataProvider implements IHookDataProvider {
    private final String type;
    
    private final ModConfigSpec.IntValue countProvider;
    private final ModConfigSpec.DoubleValue rangeProvider;
    private final ModConfigSpec.DoubleValue speedProvider;
    private final ModConfigSpec.DoubleValue pullSpeedProvider;
    private final ModConfigSpec.BooleanValue isCreativeProvider;
    
    private Supplier<Boolean> useParticles = () -> false;
    private Supplier<ParticleOptions> particleType;
    private int minParticlesPerBlock;
    private int maxParticlesPerBlock;
    private double radius;
    private int ticksBetweenSpawns;
    
    
    public ConfigHookDataProvider(String type, ModConfigSpec.Builder builder, int count, double range, double travelSpeed, double pullSpeed, boolean isCreative) {
        this.type = type;
        this.countProvider = builder.comment("The number of hooks")
                .defineInRange("count", count, 1, Integer.MAX_VALUE);
        this.rangeProvider = builder.comment("The hooks range")
                .defineInRange("range", range, 1, Double.MAX_VALUE);
        this.speedProvider = builder.comment("The hooks shooting speed")
                .defineInRange("travelSpeed", travelSpeed, 0.1, Double.MAX_VALUE);
        this.pullSpeedProvider = builder.comment("How fast the hook pulls the player")
                .defineInRange("pullSpeed", pullSpeed, 0.1, Double.MAX_VALUE);
        this.isCreativeProvider = builder.comment("Should the hook provide creative flight or not")
                .define("creativeFlight", isCreative);
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public int count() {
        return countProvider.get();
    }

    @Override
    public float range() {
        return rangeProvider.get().floatValue();
    }

    @Override
    public float speed() {
        return speedProvider.get().floatValue();
    }

    @Override
    public float pullSpeed() {
        return pullSpeedProvider.get().floatValue();
    }

    @Override
    public boolean isCreative() {
        return isCreativeProvider.get();
    }

    @Override
    public boolean useParticles() {
        return useParticles.get();
    }

    @Override
    public Supplier<ParticleOptions> particleType() {
        return particleType;
    }

    @Override
    public int minParticlesPerBlock() {
        return minParticlesPerBlock;
    }

    @Override
    public int maxParticlesPerBlock() {
        return maxParticlesPerBlock;
    }

    @Override
    public double radius() {
        return radius;
    }

    @Override
    public int ticksBetweenSpawns() {
        return ticksBetweenSpawns;
    }

    @Override
    public void setUseParticles(Supplier<Boolean> useParticles) {
        this.useParticles = useParticles;
    }

    @Override
    public IHookDataProvider setParticleType(Supplier<ParticleOptions> particleType) {
        this.particleType = particleType;
        return this;
    }

    @Override
    public IHookDataProvider setMinParticlesPerBlock(int minParticlesPerBlock) {
        this.minParticlesPerBlock = minParticlesPerBlock;
        return this;
    }

    @Override
    public IHookDataProvider setMaxParticlesPerBlock(int maxParticlesPerBlock) {
        this.maxParticlesPerBlock = maxParticlesPerBlock;
        return this;
    }

    @Override
    public IHookDataProvider setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public IHookDataProvider setTicksBetweenSpawns(int ticksBetweenSpawns) {
        this.ticksBetweenSpawns = ticksBetweenSpawns;
        return this;
    }
}

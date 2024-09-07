package com.oe.rehooked.particle.hook.impl;

import com.oe.rehooked.particle.hook.def.HookColoredParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class RedHookParticles extends HookColoredParticles {
    protected RedHookParticles(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ, spriteSet, pXSpeed, pYSpeed, pZSpeed);
        setColor(0.86f, 0, 0);
        this.setAlpha(0.5f);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new RedHookParticles(pLevel, pX, pY, pZ, spriteSet, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}

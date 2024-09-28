package com.oe.rehooked.particle.hook.impl;

import com.oe.rehooked.particle.hook.def.HookColoredParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.Random;

public class EnderHookParticles extends HookColoredParticles {
    
    protected EnderHookParticles(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ, spriteSet, pXSpeed, pYSpeed, pZSpeed);
        float colMod = (float) Math.random() * 0.6f + 0.4f;
        this.rCol = colMod * 0.9f;
        this.gCol = colMod * 0.3f;
        this.bCol = colMod;
        this.lifetime = (int) (Math.random() * 4);
        this.setAlpha(1);
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

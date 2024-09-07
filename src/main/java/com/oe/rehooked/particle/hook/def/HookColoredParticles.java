package com.oe.rehooked.particle.hook.def;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class HookColoredParticles extends TextureSheetParticle {
    
    protected HookColoredParticles(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        
        this.friction = 0f;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        
        this.quadSize *= 0.35f;
        this.lifetime = 1;
        this.setSpriteFromAge(spriteSet);
        this.gravity = 0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        
        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }
        
        public Particle createParticle(SimpleParticleType particleType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new HookColoredParticles(pLevel, pX, pY, pZ, spriteSet, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}

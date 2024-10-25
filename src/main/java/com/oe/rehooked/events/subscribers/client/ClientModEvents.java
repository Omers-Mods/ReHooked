package com.oe.rehooked.events.subscribers.client;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.client.KeyBindings;
import com.oe.rehooked.entities.ReHookedEntities;
import com.oe.rehooked.entities.hook.HookEntityModel;
import com.oe.rehooked.entities.hook.HookEntityRenderer;
import com.oe.rehooked.entities.layers.ReHookedModelLayers;
import com.oe.rehooked.entities.test.TestCubeModel;
import com.oe.rehooked.entities.test.TestCubeRenderer;
import com.oe.rehooked.particle.ReHookedParticles;
import com.oe.rehooked.particle.hook.impl.EnderHookParticles;
import com.oe.rehooked.particle.hook.impl.RedHookParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.FIRE_HOOK_KEY);
        event.register(KeyBindings.RETRACT_HOOK_KEY);
        event.register(KeyBindings.REMOVE_ALL_HOOKS_KEY);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ReHookedModelLayers.HOOK_PROJECTILE_LAYER, HookEntityModel::createBodyLayer);
        event.registerLayerDefinition(ReHookedModelLayers.TEST_CUBE_LAYER, TestCubeModel::createBodyLayer);
    }
    
    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ReHookedEntities.HOOK_PROJECTILE.get(), HookEntityRenderer::new);
        event.registerEntityRenderer(ReHookedEntities.DIRECTION_CUBE.get(), TestCubeRenderer::new);
    }
    
    @SubscribeEvent
    public static void onRegisterParticleProvider(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ReHookedParticles.RED_HOOK_PARTICLE.get(), RedHookParticles.Provider::new);
        event.registerSpriteSet(ReHookedParticles.ENDER_HOOK_PARTICLE.get(), EnderHookParticles.Provider::new);
    }
}

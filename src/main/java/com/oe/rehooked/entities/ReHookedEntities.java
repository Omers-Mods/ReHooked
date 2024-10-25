package com.oe.rehooked.entities;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.entities.test.TestCubeEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReHookedEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ReHookedMod.MOD_ID);
    
    public static final DeferredHolder<EntityType<?>, EntityType<HookEntity>> HOOK_PROJECTILE =
            ENTITY_TYPES.register("hook_projectile", () -> EntityType.Builder.<HookEntity>of(HookEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE)
                    .noSave()
                    .noSummon()
                    .fireImmune()
                    .build("rehooked:hook_projectile")
            );
    
    public static final DeferredHolder<EntityType<?>, EntityType<TestCubeEntity>> DIRECTION_CUBE = 
            ENTITY_TYPES.register("direction_cube", () -> EntityType.Builder.of(TestCubeEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(8)
                    .setTrackingRange(8)
                    .updateInterval(20)
                    .noSave()
                    .build("direction_cube")
            );
    
    public static void init(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

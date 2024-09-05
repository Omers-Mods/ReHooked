package com.oe.rehooked.entities;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.entities.test.TestCubeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ReHookedEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<EntityType<HookEntity>> HOOK_PROJECTILE =
            ENTITY_TYPES.register("hook_projectile", () -> EntityType.Builder.<HookEntity>of(HookEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(12)
                    .updateInterval(Integer.MAX_VALUE)
                    .noSave()
                    .build("hook_projectile")
            );
    
    public static final RegistryObject<EntityType<TestCubeEntity>> DIRECTION_CUBE = 
            ENTITY_TYPES.register("direction_cube", () -> EntityType.Builder.of(TestCubeEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(8)
                    .setTrackingRange(8)
                    .updateInterval(20)
                    .noSave()
                    .build("direction_cube")
            );
    
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

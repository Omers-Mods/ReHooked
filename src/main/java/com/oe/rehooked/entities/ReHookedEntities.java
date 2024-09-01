package com.oe.rehooked.entities;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.entities.hook.HookEntity;
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
                    .sized(0.15f, 0.15f)
                    .clientTrackingRange(80)
                    .setTrackingRange(80)
                    .updateInterval(Integer.MAX_VALUE)
                    .setShouldReceiveVelocityUpdates(true)
                    .noSave()
                    .build("hook_projectile")
            );
    
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

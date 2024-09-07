package com.oe.rehooked.sound;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ReHookedSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = 
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<SoundEvent> HOOK_HIT = createEvent("hook_hit");
    public static final RegistryObject<SoundEvent> HOOK_RETRACT = createEvent("hook_retract");
    public static final RegistryObject<SoundEvent> HOOK_SHOOT = createEvent("hook_shoot");
    
    private static RegistryObject<SoundEvent> createEvent(String name) {
        ResourceLocation loc = new ResourceLocation(ReHookedMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(loc));
    }
    
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}

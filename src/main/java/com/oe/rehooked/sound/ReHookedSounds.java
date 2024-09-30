package com.oe.rehooked.sound;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ReHookedSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = 
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<SoundEvent> HOOK_HIT = createEvent("hook_hit");
    public static final RegistryObject<SoundEvent> HOOK_MISS = createEvent("hook_miss");
    public static final RegistryObject<SoundEvent> HOOK_SHOOT = createEvent("hook_shoot");
    public static final RegistryObject<SoundEvent> HOOK_RETRACT = createEvent("hook_retract");
    
    private static RegistryObject<SoundEvent> createEvent(String name) {
        ResourceLocation loc = new ResourceLocation(ReHookedMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(loc));
    }
    
    public static void Init(IEventBus eventBus) {
        SoundEvents.add(HOOK_HIT);
        SoundEvents.add(HOOK_MISS);
        SoundEvents.add(HOOK_SHOOT);
        SoundEvents.add(HOOK_RETRACT);
        
        SOUND_EVENTS.register(eventBus);
    }
    
    private static final List<RegistryObject<SoundEvent>> SoundEvents = new ArrayList<>();
    
    public static RegistryObject<SoundEvent> GetEvent(int index) {
        return SoundEvents.get(index);
    }
    
    public static int GetIndex(RegistryObject<SoundEvent> registry) {
        return SoundEvents.indexOf(registry);
    }
}

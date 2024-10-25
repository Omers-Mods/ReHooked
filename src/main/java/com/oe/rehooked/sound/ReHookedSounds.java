package com.oe.rehooked.sound;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ReHookedSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = 
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ReHookedMod.MOD_ID);
    
    public static final DeferredHolder<SoundEvent, SoundEvent> HOOK_HIT = createEvent("hook_hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> HOOK_MISS = createEvent("hook_miss");
    public static final DeferredHolder<SoundEvent, SoundEvent> HOOK_SHOOT = createEvent("hook_shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> HOOK_RETRACT = createEvent("hook_retract");
    
    private static DeferredHolder<SoundEvent, SoundEvent> createEvent(String name) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(ReHookedMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(loc));
    }
    
    public static void init(IEventBus eventBus) {
        SoundEvents.add(HOOK_HIT);
        SoundEvents.add(HOOK_MISS);
        SoundEvents.add(HOOK_SHOOT);
        SoundEvents.add(HOOK_RETRACT);
        
        SOUND_EVENTS.register(eventBus);
    }
    
    private static final List<DeferredHolder<SoundEvent, SoundEvent>> SoundEvents = new ArrayList<>();
    
    public static DeferredHolder<SoundEvent, SoundEvent> GetEvent(int index) {
        return SoundEvents.get(index);
    }
    
    public static int GetIndex(DeferredHolder<SoundEvent, SoundEvent> registry) {
        return SoundEvents.indexOf(registry);
    }
}

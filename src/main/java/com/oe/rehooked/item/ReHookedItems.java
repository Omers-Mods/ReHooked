package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.particle.ReHookedParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class ReHookedItems {
    public static final String WOOD = "wood";
    public static final String IRON = "iron";
    public static final String DIAMOND = "diamond";
    public static final String RED = "red";
    public static final String ENDER = "ender";
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<Item> WOOD_HOOK = CreateHookItem(WOOD);
    public static final RegistryObject<Item> IRON_HOOK = CreateHookItem(IRON);
    public static final RegistryObject<Item> DIAMOND_HOOK = CreateHookItem(DIAMOND);
    public static final RegistryObject<Item> RED_HOOK = CreateHookItem(RED);
    public static final RegistryObject<Item> ENDER_HOOK = CreateHookItem(ENDER);
    
    private static RegistryObject<Item> CreateHookItem(String type) {
        return ITEMS.register(type + "_hook", () -> new HookItem(type));
    }
    
    public static void Init(IEventBus eventBus) {
        // register the objects
        ITEMS.register(eventBus);
    }
    
    private static ResourceLocation getHookTexture(String name) {
        return new ResourceLocation(ReHookedMod.MOD_ID, "textures/entity/hook/" + name + "/" + name + ".png");
    }
}

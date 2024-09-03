package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.item.hook.HookItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ReHookedItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<Item> WOOD_HOOK = 
            ITEMS.register("wood_hook", () -> new HookItem("wood"));
    public static final RegistryObject<Item> IRON_HOOK = 
            ITEMS.register("iron_hook", () -> new HookItem("iron"));
    public static final RegistryObject<Item> DIAMOND_HOOK =
            ITEMS.register("diamond_hook", () -> new HookItem("diamond"));
    public static final RegistryObject<Item> RED_HOOK =
            ITEMS.register("red_hook", () -> new HookItem("red"));
    public static final RegistryObject<Item> ENDER_HOOK =
            ITEMS.register("ender_hook", () -> new HookItem("ender"));
    
    public static void register(IEventBus eventBus) {
        // define all hook variants
        HookRegistry.registerHook("wood", new HookData(1, 16, 12, 6, false, getHookTexture("wood")));
        HookRegistry.registerHook("iron", new HookData(2, 32, 24, 12, false, getHookTexture("iron")));
        HookRegistry.registerHook("diamond", new HookData(4, 64, 48, 24, false, getHookTexture("diamond")));
        HookRegistry.registerHook("ender", new HookData(1, 96, Float.MAX_VALUE, 32, false, getHookTexture("ender")));
        HookRegistry.registerHook("red", new HookData(4, 32, 24, 0, true, getHookTexture("red")));
        // register the objects
        ITEMS.register(eventBus);
    }
    
    private static ResourceLocation getHookTexture(String name) {
        return new ResourceLocation(ReHookedMod.MOD_ID, "textures/entity/hook/" + name + "/" + name + ".png");
    }
}

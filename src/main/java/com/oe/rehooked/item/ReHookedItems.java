package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.item.hook.HookItem;
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
        HookRegistry.registerHook("wood", new HookData(1, 16, 8, 4));
        HookRegistry.registerHook("iron", new HookData(2, 32, 16, 8));
        HookRegistry.registerHook("diamond", new HookData(4, 64, 32, 12));
        HookRegistry.registerHook("ender", new HookData(1, 128, Float.MAX_VALUE, 32));
        HookRegistry.registerHook("red", new HookData(4, 32, 24, 0));
        // register the objects
        ITEMS.register(eventBus);
    }
}

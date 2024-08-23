package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.item.hooks.impl.DiamondHookItem;
import com.oe.rehooked.item.hooks.impl.IronHookItem;
import com.oe.rehooked.item.hooks.impl.RedHookItem;
import com.oe.rehooked.item.hooks.impl.WoodHookItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ReHookedItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<Item> WOOD_HOOK = ITEMS.register(WoodHookItem.NAME, WoodHookItem::new);
    public static final RegistryObject<Item> IRON_HOOK = ITEMS.register(IronHookItem.NAME, IronHookItem::new);
    public static final RegistryObject<Item> DIAMOND_HOOK = ITEMS.register(DiamondHookItem.NAME, DiamondHookItem::new);
    public static final RegistryObject<Item> RED_HOOK = ITEMS.register(RedHookItem.NAME, RedHookItem::new);
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

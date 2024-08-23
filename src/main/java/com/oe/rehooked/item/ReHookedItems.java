package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.item.hooks.HookItem;
import com.oe.rehooked.item.hooks.HookTypeRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;

public class ReHookedItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ReHookedMod.MOD_ID);
    
    public static final List<RegistryObject<Item>> HOOK_ITEMS = Arrays.stream(HookTypeRegistry.values())
            .map(hook -> ITEMS.register(hook.Name().toLowerCase().replace(" ", "_"),
                    () -> (Item) new HookItem(hook)))
            .toList();
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

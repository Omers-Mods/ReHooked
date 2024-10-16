package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.item.component.DiamondChain;
import com.oe.rehooked.item.component.WoodChain;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ReHookedComponents {
    public static final DeferredRegister<Item> COMPONENTS = DeferredRegister.create(ForgeRegistries.ITEMS, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<Item> WOOD_CHAIN = COMPONENTS.register("wood_chain", WoodChain::new);
    public static final RegistryObject<Item> DIAMOND_CHAIN = COMPONENTS.register("diamond_chain", DiamondChain::new);
    
    public static void Init(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}

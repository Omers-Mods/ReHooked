package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.block.ReHookedBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class ReHookedComponents {
    public static final DeferredRegister<Item> COMPONENTS = DeferredRegister.create(BuiltInRegistries.ITEM, ReHookedMod.MOD_ID);
    
    public static final DeferredHolder<Item, Item> WOOD_CHAIN = registerChainComponent("wood_chain");
    public static final DeferredHolder<Item, Item> DIAMOND_CHAIN = registerChainComponent("diamond_chain");
    public static final DeferredHolder<Item, Item> BLAZE_CHAIN = registerChainComponent("blaze_chain");
    public static final DeferredHolder<Item, Item> ENDER_CHAIN = registerChainComponent("ender_chain");
    
    public static void init(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
    
    private static DeferredHolder<Item, Item> registerChainComponent(String name) {
        Optional<DeferredHolder<Block, ? extends Block>> blockObj = ReHookedBlocks.BLOCKS.getEntries().stream().filter(regObj -> regObj.getId().getPath().equals(name)).findFirst();
        if (blockObj.isPresent() && false)
            return COMPONENTS.register(name, () -> new BlockItem(blockObj.get().get(), new Item.Properties()));
        else
            return COMPONENTS.register(name, () -> new Item(new Item.Properties()));
    }
}

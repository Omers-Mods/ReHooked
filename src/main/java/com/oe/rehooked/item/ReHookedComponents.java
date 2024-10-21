package com.oe.rehooked.item;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.block.ReHookedBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class ReHookedComponents {
    public static final DeferredRegister<Item> COMPONENTS = DeferredRegister.create(ForgeRegistries.ITEMS, ReHookedMod.MOD_ID);
    
    public static final RegistryObject<Item> WOOD_CHAIN = registerChainComponent("wood_chain");
    public static final RegistryObject<Item> DIAMOND_CHAIN = registerChainComponent("diamond_chain");
    public static final RegistryObject<Item> BLAZE_CHAIN = registerChainComponent("blaze_chain");
    public static final RegistryObject<Item> ENDER_CHAIN = registerChainComponent("ender_chain");
    
    public static void Init(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
    
    private static RegistryObject<Item> registerChainComponent(String name) {
        Optional<RegistryObject<Block>> blockObj = ReHookedBlocks.BLOCKS.getEntries().stream().filter(regObj -> regObj.getId().getPath().equals(name)).findFirst();
        if (blockObj.isPresent())
            return COMPONENTS.register(name, () -> new BlockItem(blockObj.get().get(), new Item.Properties()));
        else
            return COMPONENTS.register(name, () -> new Item(new Item.Properties()));
    }
}

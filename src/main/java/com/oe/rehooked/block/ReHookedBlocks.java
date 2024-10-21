package com.oe.rehooked.block;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.data.ChainRegistry;
import com.oe.rehooked.item.ReHookedItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ReHookedBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ReHookedMod.MOD_ID);

    public static final RegistryObject<Block> WOOD_CHAIN = BLOCKS.register("wood_chain", () -> new ChainBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN)));
    public static final RegistryObject<Block> DIAMOND_CHAIN = BLOCKS.register("diamond_chain", () -> new ChainBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN)));
    public static final RegistryObject<Block> BLAZE_CHAIN = BLOCKS.register("blaze_chain", () -> new ChainBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN)));
    public static final RegistryObject<Block> ENDER_CHAIN = BLOCKS.register("ender_chain", () -> new ChainBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN)));
    
    public static void Init(IEventBus eventBus) {
        BLOCKS.register(eventBus);

        ChainRegistry.registerChain(ReHookedItems.WOOD, WOOD_CHAIN);
        ChainRegistry.registerChain(ReHookedItems.DIAMOND, DIAMOND_CHAIN);
        ChainRegistry.registerChain(ReHookedItems.ENDER, ENDER_CHAIN);
    }
}

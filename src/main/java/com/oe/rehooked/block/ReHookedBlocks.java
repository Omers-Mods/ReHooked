package com.oe.rehooked.block;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.data.ChainRegistry;
import com.oe.rehooked.item.ReHookedItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReHookedBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, ReHookedMod.MOD_ID);
    
    public static final BlockBehaviour.Properties CHAIN_PROPS = BlockBehaviour.Properties.of().forceSolidOn().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.CHAIN).noOcclusion();
    public static final DeferredHolder<Block, Block> WOOD_CHAIN = BLOCKS.register("wood_chain", () -> new ChainBlock(CHAIN_PROPS));
    public static final DeferredHolder<Block, Block> DIAMOND_CHAIN = BLOCKS.register("diamond_chain", () -> new ChainBlock(CHAIN_PROPS));
    public static final DeferredHolder<Block, Block> BLAZE_CHAIN = BLOCKS.register("blaze_chain", () -> new ChainBlock(CHAIN_PROPS));
    public static final DeferredHolder<Block, Block> ENDER_CHAIN = BLOCKS.register("ender_chain", () -> new ChainBlock(CHAIN_PROPS));
    
    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);

        ChainRegistry.registerChain(ReHookedItems.WOOD, WOOD_CHAIN);
        ChainRegistry.registerChain(ReHookedItems.DIAMOND, DIAMOND_CHAIN);
        ChainRegistry.registerChain(ReHookedItems.BLAZE, BLAZE_CHAIN);
        ChainRegistry.registerChain(ReHookedItems.ENDER, ENDER_CHAIN);
    }
}

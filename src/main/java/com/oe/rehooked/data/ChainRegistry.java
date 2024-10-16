package com.oe.rehooked.data;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ChainRegistry {
    private static final Map<String, Supplier<Block>> CHAINS = new HashMap<>();

    public static void registerChain(String hookType, Supplier<Block> blockSupplier) {
        CHAINS.put(hookType, blockSupplier);
    }

    public static Block getChain(String hookType) {
        return Optional.ofNullable(CHAINS.get(hookType)).map(Supplier::get).orElse(Blocks.CHAIN);
    }
}

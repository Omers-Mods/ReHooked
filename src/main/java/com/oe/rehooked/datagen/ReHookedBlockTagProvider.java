package com.oe.rehooked.datagen;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ReHookedBlockTagProvider extends BlockTagsProvider {
    public ReHookedBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ReHookedMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        
    }
}

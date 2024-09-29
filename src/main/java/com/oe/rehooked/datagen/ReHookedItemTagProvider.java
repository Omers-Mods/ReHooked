package com.oe.rehooked.datagen;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.item.ReHookedItems;
import com.oe.rehooked.tags.ReHookedTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ReHookedItemTagProvider extends ItemTagsProvider {
    public ReHookedItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, ReHookedMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ReHookedTags.Items.HOOK)
                .add(ReHookedItems.WOOD_HOOK.get())
                .add(ReHookedItems.IRON_HOOK.get())
                .add(ReHookedItems.DIAMOND_HOOK.get())
                .add(ReHookedItems.RED_HOOK.get())
                .add(ReHookedItems.BLAZING_HOOK.get())
                .add(ReHookedItems.ENDER_HOOK.get())
                .add(ReHookedItems.DEJA_VHUK.get());
    }

    @Override
    public @NotNull String getName() {
        return "Item Tags";
    }
}

package com.oe.rehooked.datagen;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.item.ReHookedComponents;
import com.oe.rehooked.item.ReHookedItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ReHookedItemModelProvider extends ItemModelProvider {
    public ReHookedItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ReHookedMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ReHookedItems.WOOD_HOOK);
        simpleItem(ReHookedItems.IRON_HOOK);
        simpleItem(ReHookedItems.DIAMOND_HOOK);
        simpleItem(ReHookedItems.RED_HOOK);
        simpleItem(ReHookedItems.BLAZE_HOOK);
        simpleItem(ReHookedItems.ENDER_HOOK);
        
        componentItem(ReHookedComponents.WOOD_CHAIN);
        componentItem(ReHookedComponents.DIAMOND_CHAIN);
        componentItem(ReHookedComponents.BLAZE_CHAIN);
        componentItem(ReHookedComponents.ENDER_CHAIN);
    }
    
    private void simpleItem(DeferredHolder<Item, ? extends Item> item) {
        withExistingParent(item.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + item.getId().getPath()));
    }
    
    private void componentItem(DeferredHolder<Item, ? extends Item> item) {
        withExistingParent(item.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/component/" + item.getId().getPath()));
    }
}

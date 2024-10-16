package com.oe.rehooked.datagen;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.item.ReHookedItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

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
    }
    
    private void simpleItem(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(), new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(ReHookedMod.MOD_ID, "item/" + item.getId().getPath()));
    }
}

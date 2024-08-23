package com.oe.rehooked.datagen;

import com.oe.rehooked.item.ReHookedItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ReHookedItems.WOOD_HOOK.get())
                .pattern("###")
                .pattern(" ##")
                .pattern("# #")
                .define('#', Items.STICK)
                .unlockedBy("has_sticks", 
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.STICK).build()))
                .save(pWriter);
    }
}

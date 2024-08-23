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
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ReHookedItems.IRON_HOOK.get())
                .pattern("IIP")
                .pattern(" HI")
                .pattern("C I")
                .define('C', Items.CHAIN)
                .define('I', Items.IRON_INGOT)
                .define('P', Items.IRON_PICKAXE)
                .define('H', ReHookedItems.WOOD_HOOK.get())
                .unlockedBy("has_wooden_hook", 
                        inventoryTrigger(ItemPredicate.Builder.item().of(ReHookedItems.WOOD_HOOK.get()).build()))
                .save(pWriter);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ReHookedItems.DIAMOND_HOOK.get())
                .pattern("DDP")
                .pattern(" HD")
                .pattern("C D")
                .define('C', Items.CHAIN)
                .define('D', Items.DIAMOND)
                .define('P', Items.DIAMOND_PICKAXE)
                .define('H', ReHookedItems.IRON_HOOK.get())
                .unlockedBy("has_iron_hook", 
                        inventoryTrigger(ItemPredicate.Builder.item().of(ReHookedItems.IRON_HOOK.get()).build()))
                .save(pWriter);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ReHookedItems.RED_HOOK.get())
                .pattern("HRR")
                .pattern(" CR")
                .pattern("C H")
                .define('C', Items.CHAIN)
                .define('R', Items.REDSTONE_BLOCK)
                .define('H', ReHookedItems.DIAMOND_HOOK.get())
                .unlockedBy("has_diamond_hook",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ReHookedItems.DIAMOND_HOOK.get()).build()))
                .save(pWriter);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ReHookedItems.ENDER_HOOK.get())
                .pattern("EEH")
                .pattern(" PE")
                .pattern("P E")
                .define('P', Items.ENDER_PEARL)
                .define('E', Items.ENDER_EYE)
                .define('H', ReHookedItems.DIAMOND_HOOK.get())
                .unlockedBy("has_diamond_hook",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ReHookedItems.DIAMOND_HOOK.get()).build()))
                .save(pWriter);
    }
}

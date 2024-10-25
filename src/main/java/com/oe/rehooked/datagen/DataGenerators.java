package com.oe.rehooked.datagen;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ReHookedRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeClient(), new ReHookedItemModelProvider(packOutput, existingFileHelper));
        ReHookedBlockTagProvider blockTagProvider = new ReHookedBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagProvider);
        generator.addProvider(event.includeServer(), new ReHookedItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));
    }
}

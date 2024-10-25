package com.oe.rehooked.tags;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ReHookedTags {
    public static class Items {
        public static final TagKey<Item> HOOK = curiosTag("hook");
        
        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(ReHookedMod.MOD_ID, name));
        }

        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
        }
        
        private static TagKey<Item> curiosTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", name));
        }
    }
}

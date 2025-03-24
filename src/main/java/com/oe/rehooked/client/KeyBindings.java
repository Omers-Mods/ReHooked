package com.oe.rehooked.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.oe.rehooked.ReHookedMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.apache.commons.lang3.StringUtils;

public class KeyBindings {
    private static final String CATEGORY = "key.categories." + ReHookedMod.MOD_ID;
    
    public static final KeyMapping FIRE_HOOK_KEY = new KeyMapping(
            "key." + ReHookedMod.MOD_ID + ".fire_hook_key",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_C, -1),
            CATEGORY
    );
    
    public static final KeyMapping RETRACT_HOOK_KEY = new KeyMapping(
            "key." + ReHookedMod.MOD_ID + ".retract_hook_key",
            KeyConflictContext.IN_GAME,
            KeyModifier.SHIFT,
            InputConstants.getKey(InputConstants.KEY_C, -1),
            CATEGORY
    );
    
    public static final KeyMapping REMOVE_ALL_HOOKS_KEY = new KeyMapping(
            "key." + ReHookedMod.MOD_ID + ".remove_all_hooks_key",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_SPACE, -1),
            CATEGORY
    );

    public static String getCombinedKeyName(KeyMapping mapping, boolean capitalize) {
        var builder = new StringBuilder();
        if (!mapping.getKeyModifier().equals(KeyModifier.NONE)) {
            var lower = mapping.getKeyModifier().name().toLowerCase();
            builder.append(capitalize ? StringUtils.capitalize(lower) : lower).append(" + ");
        }
        var lower = mapping.getKey().getDisplayName().getString().toLowerCase();
        builder.append(capitalize ? StringUtils.capitalize(lower) : lower);
        return builder.toString();
    }

    public static Component getKeyBindComponent(KeyMapping mapping) {
        return getKeyBindComponent(mapping, true);
    }

    public static Component getKeyBindComponent(KeyMapping mapping, boolean capitalize) {
        return getKeyBindComponent(mapping, capitalize, Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    public static Component getKeyBindComponent(KeyMapping mapping, boolean capitalize, Style style) {
        return Component.literal(getCombinedKeyName(mapping, capitalize)).withStyle(style);
    }
}

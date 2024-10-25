package com.oe.rehooked.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.oe.rehooked.ReHookedMod;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

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
    
    public static String getCombinedKeyName(KeyMapping mapping) {
        StringBuilder builder = new StringBuilder();
        if (!mapping.getKeyModifier().equals(KeyModifier.NONE)) 
            builder.append(mapping.getKeyModifier().name().toLowerCase()).append(" + ");
        builder.append(mapping.getKey().getDisplayName().getString().toLowerCase());
        return builder.toString();
    }
}

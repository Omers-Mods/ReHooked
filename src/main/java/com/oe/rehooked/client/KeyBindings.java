package com.oe.rehooked.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.oe.rehooked.ReHookedMod;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBindings {
    private static final String CATEGORY = "key.categories." + ReHookedMod.MOD_ID;
    
    public static final KeyMapping FIRE_HOOK_KEY = new KeyMapping(
            "key." + ReHookedMod.MOD_ID + ".fire_hook_key",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_C, -1),
            CATEGORY
    );
}

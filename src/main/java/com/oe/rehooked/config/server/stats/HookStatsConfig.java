package com.oe.rehooked.config.server.stats;

import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.item.ReHookedItems;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.StringUtils;

public class HookStatsConfig {

    private static void createHookCategory(String hookType, ModConfigSpec.Builder builder, int count, float range, float travelSpeed, float pullSpeed, boolean isCreative) {
        builder.comment(StringUtils.capitalize(hookType) + " Hook Stats");
        builder.push(hookType);
        
        var data = new ConfigHookDataProvider(hookType, builder, 
                count,
                range,
                travelSpeed, 
                pullSpeed,
                isCreative);
        HookRegistry.registerHook(hookType, data);
        
        builder.pop();
    }
    
    public static void init(ModConfigSpec.Builder builder) {
        builder.push("hook_stats");
        
        createHookCategory(ReHookedItems.WOOD, builder, 1, 16, 12, 6, false);
        
        createHookCategory(ReHookedItems.IRON, builder, 2, 32, 24, 12, false);
        
        createHookCategory(ReHookedItems.DIAMOND, builder, 4, 64, 48, 24, false);
        
        createHookCategory(ReHookedItems.RED, builder, 3, 16, 8, 6, true);

        createHookCategory(ReHookedItems.BLAZE, builder, 2, 64, 1920, 36, false);
        
        createHookCategory(ReHookedItems.ENDER, builder, 1, 96, 1920, 48, false);
        
        builder.pop();
    }
}

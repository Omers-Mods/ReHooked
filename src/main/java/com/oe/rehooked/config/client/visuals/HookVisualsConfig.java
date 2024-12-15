package com.oe.rehooked.config.client.visuals;

import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.item.ReHookedItems;
import net.neoforged.neoforge.common.ModConfigSpec;

public class HookVisualsConfig {
    
    public static void init(ModConfigSpec.Builder builder) {
        builder.push("visuals");
        
        ModConfigSpec.BooleanValue blazeUseParticles = builder
                .comment("Should the blaze hook use particle effects instead of chains?")
                .define("blaze_hook_particles", true);
        HookRegistry.getHookData(ReHookedItems.BLAZE)
                .ifPresent(data -> data.setUseParticles(blazeUseParticles));

        ModConfigSpec.BooleanValue enderUseParticles = builder
                .comment("Should the ender hook use particle effects instead of chains?")
                .define("ender_hook_particles", true);
        HookRegistry.getHookData(ReHookedItems.ENDER)
                .ifPresent(data -> data.setUseParticles(enderUseParticles));

        builder.pop();
    }
}

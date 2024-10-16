package com.oe.rehooked.config.server;

import com.oe.rehooked.config.server.stats.HookStatsConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ReHookedConfig {
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    
    static {
        createConfig();
    }
    
    private static void createConfig() {
        HookStatsConfig.Init(SERVER_BUILDER);
    }
    
    public static void Init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }
}

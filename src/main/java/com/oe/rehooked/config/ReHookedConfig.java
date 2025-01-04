package com.oe.rehooked.config;

import com.oe.rehooked.config.client.visuals.HookVisualsConfig;
import com.oe.rehooked.config.server.stats.HookStatsConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ReHookedConfig {
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    
    static {
        createConfig();
    }
    
    private static void createConfig() {
        // server config entries
        HookStatsConfig.init(SERVER_BUILDER);
        
        // client config entries
        HookVisualsConfig.init(CLIENT_BUILDER);
    }
    
    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }
}

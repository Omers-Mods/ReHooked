package com.oe.rehooked.config;

import com.oe.rehooked.config.client.visuals.HookVisualsConfig;
import com.oe.rehooked.config.server.stats.HookStatsConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ReHookedConfig {
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    
    static {
        createConfig();
    }
    
    private static void createConfig() {
        // server config entries
        HookStatsConfig.init(SERVER_BUILDER);
        
        // client config entries
        HookVisualsConfig.init(CLIENT_BUILDER);
    }
    
    public static void init(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
        modContainer.registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }
}

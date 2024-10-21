package com.oe.rehooked.config.client.visuals;

import com.oe.rehooked.item.ReHookedItems;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HookVisualsConfig {
    public static final String PARTICLES = "particles";
    public static final String CHAIN = "chain";
    
    private static final Map<String, ForgeConfigSpec.ConfigValue<String>> CHAIN_PARTICLE_SETTINGS = new HashMap<>();
    
    public static void Init(ForgeConfigSpec.Builder builder) {
        builder.push("visuals");

        List<String> chainOrParticle = List.of(PARTICLES, CHAIN);
        
        ForgeConfigSpec.ConfigValue<String> blazeHookLink = builder
                .comment("Should the blaze hook use the chain or particle effect? " + chainOrParticle)
                .defineInList("blaze_hook_link", PARTICLES, chainOrParticle);
        CHAIN_PARTICLE_SETTINGS.put(ReHookedItems.BLAZE, blazeHookLink);
        
        ForgeConfigSpec.ConfigValue<String> enderHookLink = builder
                .comment("Should the ender hook use the chain or particle effect? " + chainOrParticle)
                .defineInList("ender_hook_link", PARTICLES, chainOrParticle);
        CHAIN_PARTICLE_SETTINGS.put(ReHookedItems.ENDER, enderHookLink);

        builder.pop();
    }
    
    public static Optional<ForgeConfigSpec.ConfigValue<String>> getChainSetting(String hookType) {
        return Optional.ofNullable(CHAIN_PARTICLE_SETTINGS.get(hookType));
    }
}

package com.oe.rehooked.config.client.visuals;

import com.oe.rehooked.item.ReHookedItems;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HookVisualsConfig {
    public static final String PARTICLES = "particles";
    public static final String CHAIN = "chain";
    
    private static final Map<String, ModConfigSpec.ConfigValue<String>> CHAIN_PARTICLE_SETTINGS = new HashMap<>();
    
    public static void init(ModConfigSpec.Builder builder) {
        builder.push("visuals");

        List<String> chainOrParticle = List.of(PARTICLES, CHAIN);
        
        ModConfigSpec.ConfigValue<String> blazeHookLink = builder
                .comment("Should the blaze hook use the chain or particle effect? " + chainOrParticle)
                .defineInList("blaze_hook_link", PARTICLES, chainOrParticle);
        CHAIN_PARTICLE_SETTINGS.put(ReHookedItems.BLAZE, blazeHookLink);

        ModConfigSpec.ConfigValue<String> enderHookLink = builder
                .comment("Should the ender hook use the chain or particle effect? " + chainOrParticle)
                .defineInList("ender_hook_link", PARTICLES, chainOrParticle);
        CHAIN_PARTICLE_SETTINGS.put(ReHookedItems.ENDER, enderHookLink);

        builder.pop();
    }
    
    public static Optional<ModConfigSpec.ConfigValue<String>> getChainSetting(String hookType) {
        return Optional.ofNullable(CHAIN_PARTICLE_SETTINGS.get(hookType));
    }
}

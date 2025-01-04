package com.oe.rehooked.config.server.stats;

import com.oe.rehooked.data.HookData;
import com.oe.rehooked.item.ReHookedItems;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class HookStatsConfig {
    private static final Map<String, HookConfigData> HOOK_CONFIG_DATA_MAP = new HashMap<>();
    
    private static void createHookCategory(String hookType, ForgeConfigSpec.Builder builder, IncompleteHookData partialHookData) {
        builder.comment(StringUtils.capitalize(hookType) + " Hook Stats");
        builder.push(hookType);
        
        HookConfigData
                .create(builder, 
                        partialHookData.count(), 
                        partialHookData.range(),
                        partialHookData.travelSpeed(),
                        partialHookData.pullSpeed(),
                        partialHookData.isCreative())
                .registerHookData(hookType);
        
        builder.pop();
    }
    
    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("hook_stats");
        
        createHookCategory(ReHookedItems.WOOD, builder,
                new IncompleteHookData(1, 16, 12, 6, false));
        
        createHookCategory(ReHookedItems.IRON, builder,
                new IncompleteHookData(2, 32, 24, 12, false));
        
        createHookCategory(ReHookedItems.DIAMOND, builder,
                new IncompleteHookData(4, 64, 48, 24, false));
        
        createHookCategory(ReHookedItems.RED, builder,
                new IncompleteHookData(3, 16, 8, 6, true));

        createHookCategory(ReHookedItems.BLAZE, builder,
                new IncompleteHookData(2, 64, 1920, 36, false));
        
        createHookCategory(ReHookedItems.ENDER, builder, 
                new IncompleteHookData(1, 96, 1920, 48, false));
        
        builder.pop();
    }
    
    public static Optional<HookConfigData> getConfigDataForType(String hookType) {
        return Optional.ofNullable(HOOK_CONFIG_DATA_MAP.get(hookType));
    }
    
    public static class HookConfigData {
        public final ForgeConfigSpec.IntValue COUNT;
        public final ForgeConfigSpec.DoubleValue RANGE;
        public final ForgeConfigSpec.DoubleValue SPEED;
        public final ForgeConfigSpec.DoubleValue PULL_SPEED;
        public final ForgeConfigSpec.BooleanValue IS_CREATIVE;


        public HookConfigData(ForgeConfigSpec.IntValue count, ForgeConfigSpec.DoubleValue range, ForgeConfigSpec.DoubleValue speed, ForgeConfigSpec.DoubleValue pullSpeed, ForgeConfigSpec.BooleanValue isCreative) {
            COUNT = count;
            RANGE = range;
            SPEED = speed;
            PULL_SPEED = pullSpeed;
            IS_CREATIVE = isCreative;
        }
        
        public static HookConfigData create(ForgeConfigSpec.Builder builder, int count, double range, double travelSpeed, double pullSpeed, boolean isCreative) {
            return new HookConfigData(
                    builder.comment("The number of hooks")
                            .defineInRange("count", count, 1, Integer.MAX_VALUE),
                    builder.comment("The hooks range")
                            .defineInRange("range", range, 1, Double.MAX_VALUE),
                    builder.comment("The hooks shooting speed")
                            .defineInRange("travelSpeed", travelSpeed, 0.1, Double.MAX_VALUE),
                    builder.comment("How fast the hook pulls the player")
                            .defineInRange("pullSpeed", pullSpeed, 0.1, Double.MAX_VALUE),
                    builder.comment("Should the hook provide creative flight or not")
                            .define("creativeFlight", isCreative)
            );
        }
        
        public void registerHookData(String hookType) {
            HOOK_CONFIG_DATA_MAP.put(hookType, this);
        }
        
        public IncompleteHookData getData() {
            return new IncompleteHookData(
                    COUNT.get(),
                    RANGE.get().floatValue(),
                    SPEED.get().floatValue(),
                    PULL_SPEED.get().floatValue(),
                    IS_CREATIVE.get()
            );
        }
    }

    public record IncompleteHookData(int count, float range, float travelSpeed, float pullSpeed, boolean isCreative) {
        public HookData complete(String type, ResourceLocation texture, Supplier<ParticleOptions> particleType, int minParticlesPerBlock, int maxParticlesPerBlock, double radius, int ticksBetweenSpawns) {
            return new HookData(type, count, range, travelSpeed, pullSpeed, isCreative, texture, particleType, minParticlesPerBlock, maxParticlesPerBlock, radius, ticksBetweenSpawns);
        }
    }
}

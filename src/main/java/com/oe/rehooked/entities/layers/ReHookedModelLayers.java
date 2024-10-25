package com.oe.rehooked.entities.layers;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ReHookedModelLayers {
    public static final ModelLayerLocation HOOK_PROJECTILE_LAYER = 
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ReHookedMod.MOD_ID, "hook_projectile_layer"), "hook_projectile_layer");
    
    public static final ModelLayerLocation TEST_CUBE_LAYER = 
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ReHookedMod.MOD_ID, "test_cube_layer"), "test_cube_layer");
}

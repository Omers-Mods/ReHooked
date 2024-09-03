package com.oe.rehooked.data;

import net.minecraft.resources.ResourceLocation;

public record HookData(int count, float range, float speed, float pullSpeed, boolean isCreative, ResourceLocation texture) {
}

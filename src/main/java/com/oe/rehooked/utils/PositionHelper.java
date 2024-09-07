package com.oe.rehooked.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PositionHelper {
    public static Vec3 getWaistPosition(Entity entity) {
        return entity.position().add(0, entity.getEyeHeight() / 1.5, 0);
    }
}

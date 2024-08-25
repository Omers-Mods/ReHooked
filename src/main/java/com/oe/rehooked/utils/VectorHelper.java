package com.oe.rehooked.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

// Shamelessly stolen from https://github.com/Direwolf20-MC/MiningGadgets/blob/mc/1.20.1/src/main/java/com/direwolf20/mininggadgets/common/util/VectorHelper.java, thanks dire!
public class VectorHelper {
    public static BlockHitResult getLookingAt(Entity entity, double range) {
        return getLookingAt(entity, ClipContext.Fluid.NONE, range);
    }
    
    public static BlockHitResult getLookingAt(Entity entity, ClipContext.Fluid rayTraceFluid, double range) {
        Level world = entity.level();

        Vec3 look = entity.getLookAngle();
        Vec3 start = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());

        Vec3 end = new Vec3(entity.getX() + look.x * range, entity.getY() + entity.getEyeHeight() + look.y * range, entity.getZ() + look.z * range);
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, rayTraceFluid, entity);
        return world.clip(context);
    }
    
    public static BlockHitResult getFromEntityAndAngle(Entity entity, Vec3 angle, double range) {
        return getFromEntityAndAngle(entity, angle, ClipContext.Fluid.NONE, range);
    }
    
    public static BlockHitResult getFromEntityAndAngle(Entity entity, Vec3 angle, ClipContext.Fluid rayTraceFluid, double range) {
        Level world = entity.level();

        Vec3 end = entity.getEyePosition().add(angle.scale(range));
        ClipContext context = new ClipContext(entity.position(), end, ClipContext.Block.COLLIDER, rayTraceFluid, entity);
        return world.clip(context);
    }
}

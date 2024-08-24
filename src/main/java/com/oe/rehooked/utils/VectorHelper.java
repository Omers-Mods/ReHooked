package com.oe.rehooked.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

// Shamelessly stolen from https://github.com/Direwolf20-MC/MiningGadgets/blob/mc/1.20.1/src/main/java/com/direwolf20/mininggadgets/common/util/VectorHelper.java, thanks dire!
public class VectorHelper {
    public static BlockHitResult getLookingAt(Player player, ItemStack tool, double range) {
        return getLookingAt(player, ClipContext.Fluid.NONE, range);
    }
    
    public static BlockHitResult getLookingAt(Player player, ClipContext.Fluid rayTraceFluid, double range) {
        Level world = player.level();

        Vec3 look = player.getLookAngle();
        Vec3 start = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());

        Vec3 end = new Vec3(player.getX() + look.x * range, player.getY() + player.getEyeHeight() + look.y * range, player.getZ() + look.z * range);
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, rayTraceFluid, player);
        return world.clip(context);
    }
}

package com.oe.rehooked.utils;

import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class VectorHelper {
    public static BlockHitResult getFromEntityAndAngle(Entity entity, Vec3 angle, double range) {
        return getFromEntityAndAngle(entity, angle, ClipContext.Fluid.NONE, range);
    }
    
    public static BlockHitResult getFromEntityAndAngle(Entity entity, Vec3 angle, ClipContext.Fluid rayTraceFluid, double range) {
        Level world = entity.level();
        Vec3 end = entity.getEyePosition().add(angle.scale(range));
        ClipContext context = new ClipContext(entity.getEyePosition(), end, ClipContext.Block.COLLIDER, rayTraceFluid, entity);
        return world.clip(context);
    }
    
    /**
     * Based on <a href="https://github.com/coolAlias/ZeldaSwordSkills/blob/master/src/main/java/zeldaswordskills/util/TargetUtils.java">ZeldaSwordSkills</a>
     * <p> 
     * This method is client-side only!
     */
    public static <T extends Entity> Optional<T> acquireLookTarget(Class<T> clazz, Player seeker, double radius) {
        Vec3 lookAngle = seeker.getLookAngle();
        double targetX = seeker.getX();
        double targetY = seeker.getEyeY();
        double targetZ = seeker.getZ();
        double distanceTraveled = 0;

        Optional<IClientPlayerHookHandler> optHandler = IClientPlayerHookHandler.FromPlayer(seeker).resolve();
        if (optHandler.isPresent()) {
            IClientPlayerHookHandler handler = optHandler.get();
            double range = handler.getMaxHookDistance();

            while ((int) distanceTraveled < range) {
                targetX += lookAngle.x;
                targetY += lookAngle.y;
                targetZ += lookAngle.z;
                distanceTraveled += 1;
                AABB bb = new AABB(targetX-radius, targetY-radius, targetZ-radius, targetX+radius, targetY+radius, targetZ+radius);
                List<T> list = seeker.level().getEntitiesOfClass(clazz, bb);
                for (T target : list) if (target != seeker) return Optional.of(target);
            }
        }
        
        return Optional.empty();
    }
}

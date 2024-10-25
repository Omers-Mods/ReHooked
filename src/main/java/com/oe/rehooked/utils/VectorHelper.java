package com.oe.rehooked.utils;

import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class VectorHelper {
    public static BlockHitResult getFromEntityAndAngle(Entity entity, Vec3 angle, double range) {
        return getFromEntityAndAngle(entity, angle, ClipContext.Fluid.NONE, range, Entity::position);
    }
    
    public static BlockHitResult getFromEntityAndAngle(Entity entity, Vec3 angle, double range, Function<Entity, Vec3> startPositionGetter) {
        return getFromEntityAndAngle(entity, angle, ClipContext.Fluid.NONE, range, startPositionGetter);
    }
    
    public static BlockHitResult getFromEntityAndAngle(Entity entity, Vec3 angle, ClipContext.Fluid rayTraceFluid, double range, Function<Entity, Vec3> startPositionGetter) {
        Level world = entity.level();
        Vec3 end = entity.getEyePosition().add(angle.scale(range));
        ClipContext context = new ClipContext(startPositionGetter.apply(entity), end, ClipContext.Block.COLLIDER, rayTraceFluid, entity);
        return world.clip(context);
    }
    
    /**
     * Based on <a href="https://github.com/coolAlias/ZeldaSwordSkills/blob/master/src/main/java/zeldaswordskills/util/TargetUtils.java">ZeldaSwordSkills</a>
     */
    public static <T extends Entity> Optional<T> acquireLookTarget(Class<T> clazz, Player seeker, double radius) {
        Vec3 lookAngle = seeker.getLookAngle();
        double targetX = seeker.getX();
        double targetY = seeker.getEyeY();
        double targetZ = seeker.getZ();
        double distanceTraveled = 0;

        Optional<IClientPlayerHookHandler> optHandler = IClientPlayerHookHandler.fromPlayer(seeker);
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

    public static Set<Entity> EntitiesInRange(Level level, Vec3 orig, Vec3 dest, double radius, Predicate<Entity> predicate) {
        Vec3 way = orig.vectorTo(dest);
        Vec3 norm = way.normalize();
        int distanceTraveled = 0;
        double range = way.length();
        
        double lookX = orig.x;
        double lookY = orig.y;
        double lookZ = orig.z;
        Set<Entity> entities = new HashSet<>();
        while (distanceTraveled < range) {
            lookX += norm.x;
            lookY += norm.y;
            lookZ += norm.z;
            distanceTraveled++;
            AABB bb = new AABB(lookX-radius, lookY-radius, lookZ-radius, lookX+radius, lookY+radius, lookZ+radius);
            entities.addAll(level.getEntitiesOfClass(Entity.class, bb, predicate));
        }
        return entities;
    }
    
    public static class Box {
        public double lX;
        public double lY;
        public double lZ;
        public double hX;
        public double hY;
        public double hZ;
        
        public Box() {
            lX = Double.POSITIVE_INFINITY;
            hX = Double.NEGATIVE_INFINITY;
            lY = Double.POSITIVE_INFINITY;
            hY = Double.NEGATIVE_INFINITY;
            lZ = Double.POSITIVE_INFINITY;
            hZ = Double.NEGATIVE_INFINITY;
        }
        
        public void reassignPoints(Vec3... points) {
            for (Vec3 point : points) {
                if (lX > point.x) lX = point.x;
                if (hX < point.x) hX = point.x;
                if (lY > point.y) lY = point.y;
                if (hY < point.y) hY = point.y;
                if (lZ > point.z) lZ = point.z;
                if (hZ < point.z) hZ = point.z;
            }
        }
        
        public boolean isInside(Vec3 pos) {
            if (pos.x < lX || pos.x > hX) return false;
            if (pos.y < lY || pos.y > hY) return false;
            if (pos.z < lZ || pos.z > hZ) return false;
            return true;
        }
        
        public Vec3 closestPointInCube(Vec3 pos) {
            double x = Mth.clamp(pos.x, lX, hX);
            double y = Mth.clamp(pos.y, lY, hY);
            double z = Mth.clamp(pos.z, lZ, hZ);
            return new Vec3(x, y, z);
        }

        @Override
        public String toString() {
            return "low: (" + lX + ", " + lY + ", " + lZ + "), high: (" + hX + ", " + hY + ", " + hZ + ")";
        }
    }
}

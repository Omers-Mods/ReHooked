package com.oe.rehooked.entities.hook;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.entities.ReHookedEntities;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

public class HookEntity extends Projectile {
    private static final EntityDataAccessor<Integer> TICKS_TO_TRAVEL = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> TYPE =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Vector3f> DIRECTION = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Optional<BlockState>> HIT_STATE = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Optional<BlockPos>> HIT_POS =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    public HookEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HookEntity(Level pLevel, Player player) {
        super(ReHookedEntities.HOOK_PROJECTILE.get(), pLevel);
        setOwner(player);
        setNoGravity(false);
        CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(curiosInventory -> curiosInventory
                        .findFirstCurio(itemStack ->  itemStack.getItem() instanceof HookItem))
                .ifPresent(slotResult -> {
                    CompoundTag hookType = slotResult.stack().getOrCreateTag();
                    player.awardStat(Stats.ITEM_USED.get(slotResult.stack().getItem()));
                    if (hookType.contains(HookItem.HOOK_TYPE_TAG))
                        entityData.set(TYPE, hookType.getString(HookItem.HOOK_TYPE_TAG));
                });
        Vec3 move = player.getEyePosition().add(player.getLookAngle().multiply(0.5, 0.5, 0.5));
        this.moveTo(move.x, move.y, move.z, this.getYRot(), this.getXRot());
    }
    
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy, int ticks) {
        if (pVelocity == Float.MAX_VALUE) {
            // todo: handle instantaneous hooks (ender)
        }
        else {
            entityData.set(TICKS_TO_TRAVEL, ticks);
            super.shootFromRotation(pShooter, pX, pY, pZ, pVelocity / 20.0f, pInaccuracy);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // no hit in range -> destroy hook
        Optional<BlockPos> hitPos = entityData.get(HIT_POS);
        if(hitPos.isEmpty()) {
            if(this.tickCount >= entityData.get(TICKS_TO_TRAVEL)) {
                this.discard();
            }
        }

        // clean massive errors
        if (this.tickCount >= 3000) {
            this.discard();
        }

        Vec3 dV = this.getDeltaMovement();
        boolean hit = hitPos.isPresent();
        if (this.tickCount - 5 > entityData.get(TICKS_TO_TRAVEL) || hit) {
            // reached max range without hitting solid block, should destroy
            if (!hit)
                this.discard();
            else
                return;
        }
        else if (!level().isClientSide()){
            BlockHitResult hitResult = VectorHelper.getFromEntityAndAngle(this, new Vec3(dV.x, dV.y, dV.z).normalize(), dV.length());
            onHitBlock(hitResult);
        }
        
        double d0 = this.getX() + dV.x;
        double d1 = this.getY() + dV.y;
        double d2 = this.getZ() + dV.z;
        this.updateRotation();
        /*
        double d5 = vec3.x;
        double d6 = vec3.y;
        double d7 = vec3.z;

        for(int i = 1; i < 5; ++i) {
            this.level().addParticle(ModParticles.ALEXANDRITE_PARTICLES.get(), d0-(d5*2), d1-(d6*2), d2-(d7*2),
                    -d5, -d6 - 0.1D, -d7);
        }
        */
        this.setPos(d0, d1, d2);
    }
    
    

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        BlockPos blockPos = this.blockPosition();
        BlockState blockState = this.level().getBlockState(blockPos);
        if (!blockState.isAir() && blockState.getFluidState().equals(Fluids.EMPTY.defaultFluidState())) {
            VoxelShape voxelshape = blockState.getCollisionShape(this.level(), blockPos);
            if (!voxelshape.isEmpty()) {
                Vec3 currPos = this.position();

                for(AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockPos).contains(currPos)) {
                        entityData.set(HIT_STATE, Optional.of(blockState));
                        entityData.set(HIT_POS, Optional.of(pResult.getBlockPos()));
                        ReHookedMod.LOGGER.info("Hit block at: {}", pResult.getBlockPos());
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(TICKS_TO_TRAVEL, 20);
        entityData.define(TYPE, "");
        entityData.define(HIT_POS, Optional.empty());
        entityData.define(HIT_STATE, Optional.empty());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        ReHookedMod.LOGGER.info("Discarded hook!");
    }
}

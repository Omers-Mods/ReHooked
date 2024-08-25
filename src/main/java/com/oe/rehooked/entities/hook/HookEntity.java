package com.oe.rehooked.entities.hook;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.ReHookedEntities;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.utils.VectorHelper;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.CuriosApi;

public class HookEntity extends Projectile {
    private static final EntityDataAccessor<Boolean> HIT = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TICKS_TO_TRAVEL = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> TYPE =
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Vector3f> DIRECTION = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.VECTOR3);
    
    public HookEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HookEntity(Level pLevel, Player player) {
        super(ReHookedEntities.HOOK_PROJECTILE.get(), pLevel);
        setOwner(player);
        this.setNoGravity(false);
        CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(curiosInventory -> curiosInventory
                        .findFirstCurio(itemStack ->  itemStack.getItem() instanceof HookItem))
                .ifPresent(slotResult -> {
                    CompoundTag hookType = slotResult.stack().getOrCreateTag();
                    player.awardStat(Stats.ITEM_USED.get(slotResult.stack().getItem()));
                    if (hookType.contains(HookItem.HOOK_TYPE_TAG))
                        this.entityData.set(TYPE, hookType.getString(HookItem.HOOK_TYPE_TAG));
                });
    }

    @Override
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        HookRegistry.getHookData(this.entityData.get(TYPE)).ifPresent(data -> {
            float speed = data.speed();
            float adjustedSpeed = speed / 20.0f;
            Vec3 lookAngle = pShooter.getLookAngle();
            this.moveTo(pShooter.position().add(lookAngle));
            Vec3 moveVector = lookAngle.scale(adjustedSpeed);
            this.entityData.set(DIRECTION, moveVector.toVector3f());

            // todo: remove debug logs
            ReHookedMod.LOGGER.info("Hook speed: {}, scaled to tick time: {}", speed, adjustedSpeed);
            ReHookedMod.LOGGER.info("Look angle: {}", lookAngle);
            ReHookedMod.LOGGER.info("Look angle after scaling: {}", moveVector);
            
            if (speed != Float.MAX_VALUE) {
                this.entityData.set(TICKS_TO_TRAVEL, (int) Math.ceil(data.range() / adjustedSpeed));
                super.shootFromRotation(pShooter, pShooter.getXRot(), pShooter.getYRot(), 0.0F, adjustedSpeed, 0f);
            }
            else {
                BlockHitResult hitResult = VectorHelper.getLookingAt(pShooter, data.range());
                onHitBlock(hitResult);
                if (this.entityData.get(HIT)) {
                    super.shootFromRotation(pShooter, pShooter.getXRot(), pShooter.getYRot(), 0.0f,
                            (float) Math.sqrt(distanceToSqr(hitResult.getLocation())), 0);
                }
                else {
                    // no target in range, destroy
                    discard();
                }
            }
        });
    }

    @Override
    public void tick() {
        super.tick();

        Vector3f dV = this.entityData.get(DIRECTION);
        
        if (firstTick && level().isClientSide()) {
            HookRegistry.getHookData(this.entityData.get(TYPE)).ifPresent(data -> {
                Integer ticks = this.entityData.get(TICKS_TO_TRAVEL);
                lerpTo(this.getX() + dV.x * data.range(), 
                        this.getY() + dV.y * data.range(),
                        this.getZ() + dV.z * data.range(),
                        this.getYRot(), this.getXRot(),
                        ticks, false);
            });
        }
        
        // stop moving because hit block or max range
        Boolean hit = this.entityData.get(HIT);
        if (this.tickCount > this.entityData.get(TICKS_TO_TRAVEL) || hit) {
            this.setDeltaMovement(Vec3.ZERO);
            // reached max range without hitting solid block, should destroy
            if (!hit)
                this.discard();
            else
                return;
        }
        else if (!level().isClientSide()){
            HookRegistry.getHookData(this.entityData.get(TYPE)).ifPresent(data -> {
                BlockHitResult hitResult = VectorHelper.getFromEntityAndAngle(this, new Vec3(dV.x, dV.y, dV.z), dV.length());
                onHitBlock(hitResult);
            });
        }
        
        this.setPos(this.getX() + dV.x, this.getY() + dV.y, this.getZ() + dV.z);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        BlockState hitState = level().getBlockState(pResult.getBlockPos());
        if (!hitState.isAir() && hitState.getFluidState() == Fluids.EMPTY.defaultFluidState()) {
            this.entityData.set(HIT, true);
            super.onHitBlock(pResult);
            ReHookedMod.LOGGER.info("Hit block at: {}", pResult.getBlockPos());
            this.moveTo(pResult.getLocation());
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HIT, false);
        this.entityData.define(TICKS_TO_TRAVEL, 20);
        this.entityData.define(TYPE, "");
        this.entityData.define(DIRECTION, Vec3.ZERO.toVector3f());
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

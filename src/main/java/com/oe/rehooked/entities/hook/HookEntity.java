package com.oe.rehooked.entities.hook;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.entities.ReHookedEntities;
import com.oe.rehooked.item.hooks.def.BaseHookItem;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import top.theillusivec4.curios.api.CuriosApi;

public class HookEntity extends Projectile {
    private static final EntityDataAccessor<Boolean> HIT = 
            SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.BOOLEAN);
    private int ticksToTravel;
    private double speed;
    private Vec3 direction;
    
    public HookEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HookEntity(Level pLevel, Player player) {
        super(ReHookedEntities.HOOK_PROJECTILE.get(), pLevel);
        setOwner(player);
        this.setNoGravity(true);
        this.direction = player.getLookAngle();
        CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(curiosInventory -> curiosInventory
                        .findFirstCurio(itemStack ->  itemStack.getItem() instanceof BaseHookItem))
                .ifPresent(slotResult -> {
                    BaseHookItem hookItem = (BaseHookItem) slotResult.stack().getItem();
                    this.speed = hookItem.Speed();
                    this.ticksToTravel = (int) Math.ceil(hookItem.Range() / this.speed);
                });
        Vec3 lookAngle = player.getLookAngle();
        ReHookedMod.LOGGER.info("Hook speed: {}, scaled to tick time: {}", speed, speed / 20.0);
        ReHookedMod.LOGGER.info("Look angle: {}", lookAngle);
        ReHookedMod.LOGGER.info("Look angle after scaling: {}", lookAngle.scale(speed / 20.0));
        this.moveTo(player.position().add(lookAngle));
        this.setDeltaMovement(lookAngle.scale(speed / 20.0));
    }

    @Override
    public void tick() {
        super.tick();
        
        // stop moving because hit block or max range
        Boolean hit = this.entityData.get(HIT);
        if (this.tickCount > this.ticksToTravel || hit) {
            this.setDeltaMovement(Vec3.ZERO);
            // reached max range without hitting solid block, should destroy
            if (!hit)
                this.discard();
        }
        else {
            Vec3 delta = this.getDeltaMovement();
            this.setPos(this.getX() + delta.x, this.getY() + delta.y, this.getZ() + delta.z);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        BlockState hitState = level().getBlockState(pResult.getBlockPos());
        if (!hitState.isAir() && hitState.getFluidState() == Fluids.EMPTY.defaultFluidState())
            this.entityData.set(HIT, true);
    }

    @Override
    protected void onHit(HitResult pResult) {
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HIT, false);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

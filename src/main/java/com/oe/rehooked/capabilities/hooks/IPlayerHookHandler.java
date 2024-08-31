package com.oe.rehooked.capabilities.hooks;

import com.oe.rehooked.entities.hook.HookEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Collection;

@AutoRegisterCapability
public interface IPlayerHookHandler {
    void removeHook(int id);
    void removeHook(HookEntity hook);
    void removeAllHooks();
    void shootHook(float xRot, float yRot);
    IPlayerHookHandler hookType(String hookType);
    String getHookType();
    Collection<HookEntity> getPlayerHooks();
    Player getOwner();
    IPlayerHookHandler owner(Player owner);
    IPlayerHookHandler copyFrom(IPlayerHookHandler other);
    void serializeNBT(CompoundTag tag);
    void deserializeNBT(CompoundTag nbt);
    void update();
    Vec3 getMoveThisTick();
    boolean shouldMoveThisTick();
}

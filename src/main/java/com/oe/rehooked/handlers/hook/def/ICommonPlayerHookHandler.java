package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.data.HookData;
import com.oe.rehooked.entities.hook.HookEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public interface ICommonPlayerHookHandler {
    double THRESHOLD = 0.2;
    
    void addHook(int id);
    void addHook(HookEntity hookEntity);
    void removeHook(int id);
    void removeHook(HookEntity hookEntity);
    void removeAllHooks();
    void shootFromRotation(float xRot, float yRot);
    ICommonPlayerHookHandler setOwner(Player owner);
    Optional<Player> getOwner();
    Optional<HookData> getHookData();
    void update();
    boolean shouldMoveThisTick();
    Vec3 getDeltaVThisTick();
}

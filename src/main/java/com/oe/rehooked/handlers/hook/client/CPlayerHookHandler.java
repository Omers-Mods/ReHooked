package com.oe.rehooked.handlers.hook.client;

import com.oe.rehooked.data.HookData;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class CPlayerHookHandler implements ICommonPlayerHookHandler {
    @Override
    public void addHook(int id) {
        
    }

    @Override
    public void addHook(HookEntity hookEntity) {

    }

    @Override
    public void removeHook(int id) {
        
    }

    @Override
    public void removeHook(HookEntity hookEntity) {

    }

    @Override
    public void removeAllHooks() {

    }

    @Override
    public void shootFromRotation(float xRot, float yRot) {

    }

    @Override
    public ICommonPlayerHookHandler setOwner(Player owner) {
        return null;
    }

    @Override
    public Optional<Player> getOwner() {
        return Optional.empty();
    }

    @Override
    public Optional<HookData> getHookData() {
        return Optional.empty();
    }

    @Override
    public ICommonPlayerHookHandler copyOnDeath(ICommonPlayerHookHandler other) {
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public boolean shouldMoveThisTick() {
        return false;
    }

    @Override
    public Vec3 getDeltaVThisTick() {
        return null;
    }
}

package com.oe.rehooked.handlers.hook.server;

import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SPlayerHookHandler implements ICommonPlayerHookHandler {
    private final List<HookEntity> hooks;
    private Optional<Player> owner;
    private String hookType;
    
    public SPlayerHookHandler() {
        hooks = new ArrayList<>();
        owner = Optional.empty();
        hookType = "";
    }

    @Override
    public void addHook(int id) {
        
    }

    @Override
    public void addHook(HookEntity hookEntity) {

    }

    @Override
    public void removeHook(int id) {
        if (hooks.removeIf(hookId -> hookId.equals(id))) {
        }
    }

    @Override
    public void removeHook(HookEntity hookEntity) {

    }

    @Override
    public void removeAllHooks() {
        hooks.forEach(hook -> {
            hook.discard();
            // todo: send to client to remove
            hook.getId();
        });
    }

    @Override
    public void shootFromRotation(float xRot, float yRot) {
        // todo:
    }

    @Override
    public ICommonPlayerHookHandler setOwner(Player owner) {
        this.owner = Optional.of(owner);
        return this;
    }

    @Override
    public Optional<Player> getOwner() {
        return owner;
    }

    @Override
    public Optional<HookData> getHookData() {
        return HookRegistry.getHookData(hookType);
    }

    @Override
    public ICommonPlayerHookHandler copyOnDeath(ICommonPlayerHookHandler other) {
        // todo:
        return this;
    }

    @Override
    public void update() {
        // todo:
    }

    @Override
    public boolean shouldMoveThisTick() {
        // todo:
        return false;
    }

    @Override
    public Vec3 getDeltaVThisTick() {
        // todo:
        return null;
    }
}

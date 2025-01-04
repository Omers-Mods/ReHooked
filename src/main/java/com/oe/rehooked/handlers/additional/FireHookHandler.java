package com.oe.rehooked.handlers.additional;

import com.oe.rehooked.handlers.additional.def.IServerHandler;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.world.entity.Entity;

public class FireHookHandler implements IServerHandler {
    private final IServerPlayerHookHandler handler;
    
    public FireHookHandler(IServerPlayerHookHandler handler) {
        this.handler = handler;
    }

    @Override
    public void update() {
        handler.getOwner().ifPresent(owner -> 
                handler.getHooks().forEach(hook -> 
                        VectorHelper.entitiesInRange(hook.level(), owner.position(), hook.position(), 
                                        hook.getBoundingBox().getSize(), entity -> filter(owner, hook, entity))
                                .forEach(entity -> entity.setRemainingFireTicks(100))));
    }
    
    private boolean filter(Entity owner, Entity hook, Entity entity) {
        return entity != owner && entity != hook && !entity.isOnFire() && !entity.fireImmune();
    }
}

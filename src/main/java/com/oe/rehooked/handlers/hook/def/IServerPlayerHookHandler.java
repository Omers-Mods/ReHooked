package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.extensions.player.IReHookedPlayerExtension;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface IServerPlayerHookHandler extends ICommonPlayerHookHandler {
    static Optional<IServerPlayerHookHandler> fromPlayer(Player player) {
        return ((IReHookedPlayerExtension) player).reHooked$getHookHandler()
                .map(handler -> (IServerPlayerHookHandler) handler);
    }
    
    void removeAllClientHooks();

    @Override
    default void onUnequip() {
        ICommonPlayerHookHandler.super.onUnequip();
        removeAllClientHooks();
        update();
    }

    @Override
    default void onEquip() {
        ICommonPlayerHookHandler.super.onEquip();
        removeAllClientHooks();
        update();
    }
    
    @Override
    default void killHook(int id) {
        getOwner().map(Player::level).ifPresent(level -> {
            if (level.getEntity(id) instanceof HookEntity hookEntity) 
                hookEntity.discard();
        });
    }
    
    default IServerPlayerHookHandler copyFrom(IServerPlayerHookHandler handler) {
        return this;
    }
}

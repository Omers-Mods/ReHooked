package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.mixin.common.player.IReHookedPlayerExtension;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface IServerPlayerHookHandler extends ICommonPlayerHookHandler {
    static Optional<IServerPlayerHookHandler> fromPlayer(Player player) {
        return ((IReHookedPlayerExtension) player).reHooked$getHookHandler()
                .map(handler -> (IServerPlayerHookHandler) handler);
    }
    
    void removeAllClientHooks(ServerPlayer player);

    @Override
    default void onUnequip() {
        ICommonPlayerHookHandler.super.onUnequip();
        getOwner().ifPresent(owner -> {
            if (owner instanceof ServerPlayer player)
                removeAllClientHooks(player);
        });
        update();
    }

    @Override
    default void onEquip() {
        ICommonPlayerHookHandler.super.onEquip();
        getOwner().ifPresent(owner -> {
            if (owner instanceof ServerPlayer player)
                removeAllClientHooks(player);
        });
        update();
    }
    
    @Override
    default void killHook(int id) {
        getOwner().map(Player::level).ifPresent(level -> {
            if (level.getEntity(id) instanceof HookEntity hookEntity) 
                hookEntity.discard();
        });
    }
    
    void copyFrom(IServerPlayerHookHandler handler);
}

package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.capabilities.hooks.ServerHookCapabilityProvider;
import com.oe.rehooked.entities.hook.HookEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public interface IServerPlayerHookHandler extends ICommonPlayerHookHandler {
    static LazyOptional<IServerPlayerHookHandler> FromPlayer(Player player) {
        return player.getCapability(ServerHookCapabilityProvider.SERVER_HOOK_HANDLER);
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
}

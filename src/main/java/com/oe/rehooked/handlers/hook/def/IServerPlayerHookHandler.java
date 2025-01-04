package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.capabilities.hooks.ServerHookCapabilityProvider;
import com.oe.rehooked.entities.hook.HookEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public interface IServerPlayerHookHandler extends ICommonPlayerHookHandler {
    static LazyOptional<IServerPlayerHookHandler> FromPlayer(Player player) {
        return player.getCapability(ServerHookCapabilityProvider.SERVER_HOOK_HANDLER);
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
    
    void copyFrom(IServerPlayerHookHandler handler);
}

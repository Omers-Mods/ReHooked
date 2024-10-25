package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.mixin.common.player.IReHookedPlayerExtension;
import com.oe.rehooked.network.payloads.server.SHookPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public interface IClientPlayerHookHandler extends ICommonPlayerHookHandler {
    static Optional<IClientPlayerHookHandler> fromPlayer(Player player) {
        return ((IReHookedPlayerExtension) player).reHooked$getHookHandler()
                .map(handler -> (IClientPlayerHookHandler) handler);
    }
    
    double getMaxHookDistance();

    @Override
    default void jump() {
        getOwner().ifPresent(owner -> {
            if (owner.isCrouching()) 
                PacketDistributor.sendToServer(new SHookPayload(SHookPayload.State.RETRACT_ALL_HOOKS));
            else {
                PacketDistributor.sendToServer(new SHookPayload(SHookPayload.State.JUMP));
                ICommonPlayerHookHandler.super.jump();
            }
        });
    }

    @Override
    default void killHook(int id) {
        PacketDistributor.sendToServer(new SHookPayload(SHookPayload.State.KILL, id));
    }
}

package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.capabilities.hooks.ClientHookCapabilityProvider;
import com.oe.rehooked.network.handlers.PacketHandler;
import com.oe.rehooked.network.packets.server.SHookCapabilityPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public interface IClientPlayerHookHandler extends ICommonPlayerHookHandler {
    static LazyOptional<IClientPlayerHookHandler> FromPlayer(Player player) {
        return player.getCapability(ClientHookCapabilityProvider.CLIENT_HOOK_HANDLER);
    }

    double getMaxHookDistance();

    @Override
    default void jump() {
        getOwner().ifPresent(owner -> {
            if (owner.isCrouching()) 
                PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.State.RETRACT_ALL_HOOKS));
            else {
                PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.State.JUMP));
                ICommonPlayerHookHandler.super.jump();
            }
        });
    }

    @Override
    default void killHook(int id) {
        PacketHandler.sendToServer(new SHookCapabilityPacket(SHookCapabilityPacket.State.KILL, id));
    }
}

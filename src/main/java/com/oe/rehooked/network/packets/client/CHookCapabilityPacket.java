package com.oe.rehooked.network.packets.client;

import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.network.packets.common.HookCapabilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CHookCapabilityPacket extends HookCapabilityPacket {
    public CHookCapabilityPacket(byte packetType, int additional) {
        super(packetType, additional);
    }

    public CHookCapabilityPacket(byte packetType) {
        super(packetType);
    }

    public CHookCapabilityPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            LazyOptional<ICommonPlayerHookHandler> lazyHandler = ICommonPlayerHookHandler.FromPlayer(player);
            switch (State.Get(packetType)) {
                case ADD_HOOK -> lazyHandler.ifPresent(handler -> handler.addHook(additional));
                case RETRACT_HOOK -> lazyHandler.ifPresent(handler -> handler.removeHook(additional));
                case RETRACT_ALL_HOOKS -> lazyHandler.ifPresent(ICommonPlayerHookHandler::removeAllHooks);
                case FORCE_UPDATE -> lazyHandler.ifPresent(ICommonPlayerHookHandler::update);
            }
        });
        context.get().setPacketHandled(true);
    }
    
    public enum State {
        ADD_HOOK,
        RETRACT_HOOK,
        RETRACT_ALL_HOOKS,
        FORCE_UPDATE;
        
        public static State Get(int ordinal) {
            return State.values()[ordinal];
        }
    }
}

package com.oe.rehooked.network.packets.client;

import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.network.packets.common.HookCapabilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;
import java.util.logging.Handler;

public class CHookCapabilityPacket extends HookCapabilityPacket {
    public static final byte ADD_HOOK = 1;
    public static final byte REMOVE_HOOK = 2;
    public static final byte REMOVE_ALL = 4;
    public static final byte UPDATE = 8;
    
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
            switch (packetType) {
                case ADD_HOOK -> lazyHandler.ifPresent(handler -> handler.addHook(additional));
                case REMOVE_HOOK -> lazyHandler.ifPresent(handler -> handler.removeHook(additional));
                case REMOVE_ALL -> lazyHandler.ifPresent(ICommonPlayerHookHandler::removeAllHooks);
                case UPDATE -> lazyHandler.ifPresent(ICommonPlayerHookHandler::update);
            }
        });
        context.get().setPacketHandled(true);
    }
}

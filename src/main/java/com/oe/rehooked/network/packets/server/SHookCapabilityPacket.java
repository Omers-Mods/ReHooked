package com.oe.rehooked.network.packets.server;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.capabilities.hooks.IPlayerHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.network.packets.common.HookCapabilityPacket;
import com.oe.rehooked.utils.CurioUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class SHookCapabilityPacket extends HookCapabilityPacket {
    public static final byte SHOOT = 1;
    public static final byte RETRACT = 2;
    public static final byte ALL = 4;
    
    private static Logger LOGGER = LogUtils.getLogger();
    
    private final float xRot;
    private final float yRot;
    
    public SHookCapabilityPacket(byte packetType, int additional, float xRot, float yRot) {
        super(packetType, additional);
        this.xRot = xRot;
        this.yRot = yRot;
    }
    
    public SHookCapabilityPacket(byte packetType) {
        this(packetType, 0, 0, 0);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        super.encode(buf);
        buf.writeFloat(xRot);
        buf.writeFloat(yRot);
    }

    public SHookCapabilityPacket(FriendlyByteBuf buffer) {
        super(buffer.readByte(), buffer.readInt());
        xRot = buffer.readFloat();
        yRot = buffer.readFloat();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null || player.level().isClientSide()) return;
            IPlayerHookHandler.FromPlayer(player).ifPresent(handler -> {
                handler.owner(player);
                CurioUtils.GetCuriosOfType(HookItem.class, player).flatMap(CurioUtils::GetIfUnique).ifPresent(hookStack -> {
                    HookItem hookItem = (HookItem) hookStack.getItem();
                    switch (packetType) {
                        case SHOOT -> handler.hookType(hookItem.getHookType()).shootHook(xRot, yRot);
                        case RETRACT -> handler.hookType(hookItem.getHookType()).removeHook(additional);
                        case ALL -> handler.hookType(hookItem.getHookType()).removeAllHooks();
                    }
                });
            });
        });
        context.get().setPacketHandled(true);
    }
}

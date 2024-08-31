package com.oe.rehooked.network.packets.server;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.network.packets.common.HookCapabilityPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class SHookCapabilityPacket extends HookCapabilityPacket {
    private static Logger LOGGER = LogUtils.getLogger();
    
    private final float xRot;
    private final float yRot;
    
    public SHookCapabilityPacket(State packetType, int additional, float xRot, float yRot) {
        super(packetType.ordinal(), additional);
        this.xRot = xRot;
        this.yRot = yRot;
    }
    
    public SHookCapabilityPacket(State packetType) {
        this(packetType, 0, 0, 0);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        super.encode(buf);
        buf.writeFloat(xRot);
        buf.writeFloat(yRot);
    }

    public SHookCapabilityPacket(FriendlyByteBuf buffer) {
        super(buffer.readInt(), buffer.readInt());
        xRot = buffer.readFloat();
        yRot = buffer.readFloat();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null || player.level().isClientSide()) return;
            ICommonPlayerHookHandler.FromPlayer(player).ifPresent(handler -> {
                handler.setOwner(player);
                switch (State.Get(packetType)) {
                    case SHOOT -> handler.shootFromRotation(xRot, yRot);
                    case RETRACT_HOOK -> handler.removeHook(additional);
                    case RETRACT_ALL_HOOKS -> handler.removeAllHooks();
                }
            });
        });
        context.get().setPacketHandled(true);
    }
    
    public enum State {
        SHOOT,
        RETRACT_HOOK,
        RETRACT_ALL_HOOKS;
        
        public static State Get(int ordinal) {
            return State.values()[ordinal];
        }
    }
}

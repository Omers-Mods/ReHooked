package com.oe.rehooked.network.packets.server;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class SHookCapabilityPacket {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private final State packetType;
    private final int id;
    private final float xRot;
    private final float yRot;
    
    public SHookCapabilityPacket(State packetType, int id, float xRot, float yRot) {
        this.packetType = packetType;
        this.id = id;
        this.xRot = xRot;
        this.yRot = yRot;
    }
    
    public SHookCapabilityPacket(State packetType, int id) {
        this(packetType, id, 0, 0);
    }
    
    public SHookCapabilityPacket(State packetType) {
        this(packetType, 0);
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(packetType.ordinal());
        buf.writeInt(id);
        buf.writeFloat(xRot);
        buf.writeFloat(yRot);
    }

    public SHookCapabilityPacket(FriendlyByteBuf buf) {
        packetType = State.Get(buf.readInt());
        id = buf.readInt();
        xRot = buf.readFloat();
        yRot = buf.readFloat();
    }
    
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            LOGGER.debug("Handling server hook packet for: {}", packetType);
            ServerPlayer player = context.get().getSender();
            if (player == null) return;
            Optional<IServerPlayerHookHandler> optHandler = IServerPlayerHookHandler.FromPlayer(player).resolve();
            if (optHandler.isPresent()) {
                IServerPlayerHookHandler handler = optHandler.get();
                LOGGER.debug("Got hook handler");
                handler.setOwner(player);
                switch (packetType) {
                    case SHOOT -> handler.shootFromRotation(xRot, yRot);
                    case RETRACT_HOOK -> handler.removeHook(id);
                    case RETRACT_ALL_HOOKS -> handler.removeAllHooks();
                    case FORCE_UPDATE -> handler.update();
                }
            }
            else {
                LOGGER.debug("Missing handler capability");
            }
        });
        context.get().setPacketHandled(true);
    }
    
    public enum State {
        SHOOT,
        RETRACT_HOOK,
        RETRACT_ALL_HOOKS,
        JUMP,
        FORCE_UPDATE;
        
        public static State Get(int ordinal) {
            return State.values()[ordinal];
        }
    }
}

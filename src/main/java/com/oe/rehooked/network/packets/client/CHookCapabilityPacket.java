package com.oe.rehooked.network.packets.client;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class CHookCapabilityPacket {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private final State packetType;
    private final int id;
    
    public CHookCapabilityPacket(State packetType, int id) {
        this.packetType = packetType;
        this.id = id;
    }

    public CHookCapabilityPacket(State packetType) {
        this(packetType, 0);
    }

    public CHookCapabilityPacket(FriendlyByteBuf buf) {
        packetType = State.Get(buf.readInt());
        id = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(packetType.ordinal());
        buf.writeInt(id);
    }

    public static void handle(CHookCapabilityPacket packet,  Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> 
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> packet::handle));
        context.get().setPacketHandled(true);
    }
    
    public void handle() {
        LOGGER.debug("Handling client hook packet for: {}", packetType);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Optional<IClientPlayerHookHandler> optHandler = IClientPlayerHookHandler.FromPlayer(player).resolve();
        if (optHandler.isPresent()) {
            IClientPlayerHookHandler handler = optHandler.get();
            switch (packetType) {
                case ADD_HOOK -> handler.addHook(id);
                case RETRACT_HOOK -> handler.removeHook(id);
                case RETRACT_ALL_HOOKS -> handler.removeAllHooks();
                case FORCE_UPDATE -> handler.update();
            }
        }
        else {
            LOGGER.debug("Missing handler capability");
        }
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

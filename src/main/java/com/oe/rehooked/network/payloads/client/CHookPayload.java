package com.oe.rehooked.network.payloads.client;

import com.oe.rehooked.ReHookedMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CHookPayload(int state, int id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CHookPayload> TYPE = 
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ReHookedMod.MOD_ID, CHookPayload.class.getSimpleName().toLowerCase()));
    
    public static final StreamCodec<ByteBuf, CHookPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            CHookPayload::state,
            ByteBufCodecs.INT,
            CHookPayload::id,
            CHookPayload::new
    );

    public CHookPayload(State state, int id) {
        this(state.ordinal(), id);
    }

    public CHookPayload(State state) {
        this(state, 0);
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
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

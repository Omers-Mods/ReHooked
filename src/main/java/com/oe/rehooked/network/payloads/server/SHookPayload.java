package com.oe.rehooked.network.payloads.server;

import com.oe.rehooked.ReHookedMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SHookPayload(int state, int id, float xRot, float yRot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SHookPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ReHookedMod.MOD_ID, SHookPayload.class.getSimpleName().toLowerCase()));

    public static final StreamCodec<ByteBuf, SHookPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            SHookPayload::state,
            ByteBufCodecs.INT,
            SHookPayload::id,
            ByteBufCodecs.FLOAT,
            SHookPayload::xRot,
            ByteBufCodecs.FLOAT,
            SHookPayload::yRot,
            SHookPayload::new
    );
    
    public SHookPayload(State state, int id) {
        this(state.ordinal(), id, 0, 0);
    }
    
    public SHookPayload(State state) {
        this(state, 0);
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum State {
        SHOOT,
        RETRACT_HOOK,
        RETRACT_ALL_HOOKS,
        JUMP,
        FORCE_UPDATE,
        KILL;

        public static State get(int ordinal) {
            return State.values()[ordinal];
        }
    }
}

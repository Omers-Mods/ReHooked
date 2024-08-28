package com.oe.rehooked.network;

import com.oe.rehooked.capabilities.hooks.PlayerHookCapabilityProvider;
import com.oe.rehooked.item.hook.HookItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class SHookCapabilityPacket {
    public static final byte SHOOT = 1;
    public static final byte RETRACT = 2;
    public static final byte ALL = 4;
    
    private byte packetType;
    private int additional;
    
    public SHookCapabilityPacket(byte packetType, int additional) {
        this.packetType = packetType;
        this.additional = additional;
    }
    
    public SHookCapabilityPacket(byte packetType) {
        this(packetType, 0);
    }

    public SHookCapabilityPacket(FriendlyByteBuf buffer) {
        this.packetType = buffer.readByte();
        this.additional = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeByte(packetType);
        buffer.writeInt(additional);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null || player.level().isClientSide()) return;
        player.getCapability(PlayerHookCapabilityProvider.PLAYER_HOOK_HANDLER).ifPresent(handler -> {
            handler.owner(player);
            CuriosApi.getCuriosInventory(player)
                    .ifPresent(inventory -> inventory.findFirstCurio(itemStack -> itemStack.getItem() instanceof HookItem)
                            .ifPresent(hook -> {
                                switch (packetType) {
                                    case SHOOT -> handler.hookType(((HookItem) hook.stack().getItem()).getHookType())
                                            .shootHook();
                                    case RETRACT -> handler.hookType(((HookItem) hook.stack().getItem()).getHookType())
                                            .removeHook(additional);
                                    case ALL -> handler.hookType(((HookItem) hook.stack().getItem()).getHookType())
                                            .removeAllHooks();
                                }
                            }));
        });
    }
}

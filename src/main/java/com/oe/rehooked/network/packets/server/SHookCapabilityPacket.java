package com.oe.rehooked.network.packets.server;

import com.oe.rehooked.capabilities.hooks.IPlayerHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.network.packets.common.HookCapabilityPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class SHookCapabilityPacket extends HookCapabilityPacket {
    private float xRot;
    private float yRot;
    
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
                CuriosApi.getCuriosInventory(player)
                        .ifPresent(inventory -> inventory.findFirstCurio(itemStack -> itemStack.getItem() instanceof HookItem)
                                .ifPresent(hook -> {
                                    switch (packetType) {
                                        case SHOOT -> {
                                            player.setXRot(xRot);
                                            player.setYRot(yRot);
                                            handler.hookType(((HookItem) hook.stack().getItem()).getHookType())
                                                    .shootHook();
                                        }
                                        case RETRACT -> handler.hookType(((HookItem) hook.stack().getItem()).getHookType())
                                                .removeHook(additional);
                                        case ALL -> handler.hookType(((HookItem) hook.stack().getItem()).getHookType())
                                                .removeAllHooks();
                                    }
                                }));
            });
        });
        context.get().setPacketHandled(true);
    }
}

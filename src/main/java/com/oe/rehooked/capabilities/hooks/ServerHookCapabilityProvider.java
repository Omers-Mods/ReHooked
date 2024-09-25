package com.oe.rehooked.capabilities.hooks;

import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import com.oe.rehooked.handlers.hook.server.SPlayerHookHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerHookCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<IServerPlayerHookHandler> SERVER_HOOK_HANDLER =
            CapabilityManager.get(new CapabilityToken<>() {});

    private IServerPlayerHookHandler serverHandler = null;
    private final LazyOptional<IServerPlayerHookHandler> serverHandlerOptional = LazyOptional.of(this::createServerHookHandler);
    private IServerPlayerHookHandler createServerHookHandler() {
        if (this.serverHandler == null) this.serverHandler = new SPlayerHookHandler();
        return this.serverHandler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(SERVER_HOOK_HANDLER))
            return serverHandlerOptional.cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createServerHookHandler().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createServerHookHandler().loadNBTData(nbt);
    }
}

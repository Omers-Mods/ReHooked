package com.oe.rehooked.capabilities.hooks;

import com.oe.rehooked.handlers.hook.client.CPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientHookCapabilityProvider implements ICapabilityProvider {
    public static final Capability<IClientPlayerHookHandler> CLIENT_HOOK_HANDLER =
            CapabilityManager.get(new CapabilityToken<>() {});

    private IClientPlayerHookHandler clientHandler = null;
    private final LazyOptional<IClientPlayerHookHandler> clientHandlerOptional = LazyOptional.of(this::createClientHookHandler);
    private IClientPlayerHookHandler createClientHookHandler() {
        if (this.clientHandler == null) this.clientHandler = new CPlayerHookHandler();
        return this.clientHandler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CLIENT_HOOK_HANDLER))
            return clientHandlerOptional.cast();
        return LazyOptional.empty();
    }
}

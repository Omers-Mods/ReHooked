package com.oe.rehooked.capabilities.hooks;

import com.oe.rehooked.handlers.hook.PlayerHookHandler;
import com.oe.rehooked.handlers.hook.client.CPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.handlers.hook.server.SPlayerHookHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerHookCapabilityProvider implements ICapabilityProvider {
    public static final Capability<ICommonPlayerHookHandler> CLIENT_HOOK_HANDLER =
            CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<ICommonPlayerHookHandler> SERVER_HOOK_HANDLER =
            CapabilityManager.get(new CapabilityToken<>() {});
    
    private ICommonPlayerHookHandler clientHandler = null;
    private final LazyOptional<ICommonPlayerHookHandler> clientHandlerOptional = LazyOptional.of(this::createClientHookHandler);
    private ICommonPlayerHookHandler createClientHookHandler() {
        if (this.clientHandler == null) this.clientHandler = new CPlayerHookHandler();
        return this.clientHandler;
    }
    
    private ICommonPlayerHookHandler serverHandler = null;
    private final LazyOptional<ICommonPlayerHookHandler> serverHandlerOptional = LazyOptional.of(this::createServerHookHandler);
    private ICommonPlayerHookHandler createServerHookHandler() {
        if (this.serverHandler == null) this.serverHandler = new SPlayerHookHandler();
        return this.serverHandler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CLIENT_HOOK_HANDLER))
            return clientHandlerOptional.cast();
        else if (cap.equals(SERVER_HOOK_HANDLER))
            return serverHandlerOptional.cast();
        return LazyOptional.empty();
    }
}

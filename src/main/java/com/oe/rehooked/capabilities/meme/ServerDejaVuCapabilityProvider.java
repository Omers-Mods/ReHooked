package com.oe.rehooked.capabilities.meme;

import com.oe.rehooked.handlers.meme.server.SDejaVuHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerDejaVuCapabilityProvider implements ICapabilityProvider {
    public static final Capability<SDejaVuHandler> SERVER_DEJA_VU_HANDLER =
            CapabilityManager.get(new CapabilityToken<>() {});
    
    private SDejaVuHandler serverHandler = null;
    private final LazyOptional<SDejaVuHandler> serverHandlerOptional = LazyOptional.of(this::createServerDejaVuHandler);

    private SDejaVuHandler createServerDejaVuHandler() {
        if (this.serverHandler == null) this.serverHandler = new SDejaVuHandler();
        return this.serverHandler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(SERVER_DEJA_VU_HANDLER))
            return serverHandlerOptional.cast();
        return LazyOptional.empty();
    }
}

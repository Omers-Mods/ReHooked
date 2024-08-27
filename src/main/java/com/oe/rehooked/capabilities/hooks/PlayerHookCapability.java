package com.oe.rehooked.capabilities.hooks;

import com.oe.rehooked.ReHookedMod;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerHookCapability {
    public static final Capability<IPlayerHookHandler> HOOK_HANDLER_CAPABILITY = 
            CapabilityManager.get(new CapabilityToken<IPlayerHookHandler>() {});
    public static final ResourceLocation ID = 
            new ResourceLocation(ReHookedMod.MOD_ID, "hook_handler");
    
    public static void register(final RegisterCapabilitiesEvent event) {
        event.register(IPlayerHookHandler.class);
    }
    
    public static ICapabilityProvider createProvider(final IPlayerHookHandler playerHookHandler) {
        return new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return null;
            }
        };
    }
}

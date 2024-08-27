package com.oe.rehooked.capabilities.hooks;

import com.oe.rehooked.handlers.PlayerHookHandler;
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

public class PlayerHookCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<IPlayerHookHandler> PLAYER_HOOK_HANDLER = 
            CapabilityManager.get(new CapabilityToken<>() {});
    private IPlayerHookHandler handler = null;
    private final LazyOptional<IPlayerHookHandler> optional = LazyOptional.of(this::createPlayerHookHandler);

    private IPlayerHookHandler createPlayerHookHandler() {
        if (this.handler == null) {
            this.handler = new PlayerHookHandler();
        }
        return this.handler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_HOOK_HANDLER)
            return optional.cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createPlayerHookHandler().serializeNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerHookHandler().deserializeNBT(nbt);
    }
}

package com.oe.rehooked.mixin.common.player;

import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.extensions.player.IReHookedPlayerExtension;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.extensions.IPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayerExtension, IReHookedPlayerExtension {
    @Unique
    private ICommonPlayerHookHandler reHooked$hookHandler;

    @Unique
    public Optional<ICommonPlayerHookHandler> reHooked$getHookHandler() {
        return Optional.ofNullable(reHooked$hookHandler);
    }

    @Unique
    public void reHooked$setHookHandler(ICommonPlayerHookHandler hookHandler) {
        this.reHooked$hookHandler = hookHandler;
    }
}

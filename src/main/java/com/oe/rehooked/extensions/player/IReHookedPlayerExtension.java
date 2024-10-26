package com.oe.rehooked.extensions.player;

import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;

import java.util.Optional;

public interface IReHookedPlayerExtension {
    Optional<ICommonPlayerHookHandler> reHooked$getHookHandler();
    void reHooked$setHookHandler(ICommonPlayerHookHandler hookHandler);
}

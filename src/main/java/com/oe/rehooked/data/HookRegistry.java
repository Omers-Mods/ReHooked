package com.oe.rehooked.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HookRegistry {
    private static final Map<String, IHookDataProvider> HOOKS = new HashMap<>();
    
    public static void registerHook(String hookType, IHookDataProvider hookData) {
        HOOKS.put(hookType, hookData);
    }
    
    public static Optional<IHookDataProvider> getHookData(String hookType) {
        return Optional.ofNullable(HOOKS.getOrDefault(hookType, null));
    }
}

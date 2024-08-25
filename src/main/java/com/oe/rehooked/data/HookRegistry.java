package com.oe.rehooked.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HookRegistry {
    private static final Map<String, HookData> HOOKS = new HashMap<>();
    
    public static void registerHook(String hookType, HookData hookData) {
        HOOKS.put(hookType, hookData);
    }
    
    public static Optional<HookData> getHookData(String hookType) {
        return Optional.ofNullable(HOOKS.getOrDefault(hookType, null));
    }
}

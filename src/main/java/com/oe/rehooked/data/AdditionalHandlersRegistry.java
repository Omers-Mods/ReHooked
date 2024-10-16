package com.oe.rehooked.data;

import com.oe.rehooked.handlers.additional.def.ICommonHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdditionalHandlersRegistry {
    private static final Map<String, Class<? extends ICommonHandler>> HANDLERS = new HashMap<>();
    
    public static void registerHandler(String hookType, Class<? extends ICommonHandler> handler) {
        HANDLERS.put(hookType, handler);
    }
    
    public static Optional<Class<? extends ICommonHandler>> getHandler(String hookType) {
        return Optional.ofNullable(HANDLERS.get(hookType));
    }
}

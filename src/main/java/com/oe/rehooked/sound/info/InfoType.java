package com.oe.rehooked.sound.info;

import com.oe.rehooked.network.packets.server.SSoundPacket;
import com.oe.rehooked.sound.info.def.IKeepAliveInfo;

import java.lang.reflect.InvocationTargetException;

public enum InfoType {
    ;
    private final Class<? extends IKeepAliveInfo> clazz;
    InfoType(Class<? extends IKeepAliveInfo> clazz) {
        this.clazz = clazz;
    }
    
    public IKeepAliveInfo create(SSoundPacket packet) {
        try {
            return clazz.getDeclaredConstructor().newInstance().instantiate(packet);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}

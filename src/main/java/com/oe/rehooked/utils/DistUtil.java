package com.oe.rehooked.utils;

import net.minecraft.client.Minecraft;

public class DistUtil {
    public static boolean IsClient() {
        return Minecraft.getInstance().player != null;
    }
    
    public static boolean IsServer () {
        return Minecraft.getInstance().player == null;
    }
}

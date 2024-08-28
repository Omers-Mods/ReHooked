package com.oe.rehooked.events.custom;

import com.oe.rehooked.events.ForgeEventBus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.EventBus;

public class PlayerPushEvent extends PlayerEvent {
    private final Vec3 pushPower;
    
    public PlayerPushEvent(Player player, Vec3 pushPower) {
        super(player);
        this.pushPower = pushPower;
    }
    
    public Vec3 getPushPower() {
        return pushPower;
    }
}

package com.oe.rehooked.events.definition.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;

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

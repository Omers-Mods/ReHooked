package com.oe.rehooked.item.hooks.def;

import net.minecraft.world.item.Item;

public interface HookProperties {
    // The name to display on the item
    String DisplayName();
    
    // The item properties of the hook
    default Item.Properties ItemProperties() {
        return new Item.Properties().defaultDurability(0).stacksTo(1);
    }
    
    // The number of simultaneous hooks allowed
    int Count();
    
    // The maximum range the hook will travel before being deleted
    double Range();
    
    // The speed at which the hook will travel before hitting a surface
    double Speed();
    
    // The speed at which the player will be pulled into the surface hit by the hook
    double PullSpeed();
    
    // The distance from the impact point to where the chain should attach
    default double Length() {
        return 0.5;
    }
    
    // The cooldown between firing hooks in ms
    int Cooldown();
}

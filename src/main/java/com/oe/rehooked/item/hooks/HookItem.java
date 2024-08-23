package com.oe.rehooked.item.hooks;

import net.minecraft.world.item.Item;

public class HookItem extends Item implements HookProperties {
    private final HookProperties properties;
    
    public HookItem(HookProperties properties) {
        super(properties.ItemProperties());
        this.properties = properties;
    }

    @Override
    public String Name() {
        return properties.Name();
    }

    @Override
    public Properties ItemProperties() {
        return properties.ItemProperties();
    }

    @Override
    public int Count() {
        return properties.Count();
    }

    @Override
    public double Range() {
        return properties.Range();
    }

    @Override
    public double Speed() {
        return properties.Speed();
    }

    @Override
    public double PullSpeed() {
        return properties.PullSpeed();
    }

    @Override
    public double Length() {
        return properties.Length();
    }

    @Override
    public int Cooldown() {
        return properties.Cooldown();
    }
}

package com.oe.rehooked.item.hooks.def;

import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public abstract class BaseHookItem extends Item implements HookProperties, ICurioItem {
    private final HookProperties properties;
    
    public BaseHookItem(HookProperties properties) {
        super(properties.ItemProperties());
        this.properties = properties;
    }

    @Override
    public String DisplayName() {
        return properties.DisplayName();
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

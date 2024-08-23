package com.oe.rehooked.item.hooks;

import net.minecraft.world.item.Item;

public enum HookTypeRegistry implements HookProperties {
    IronHook(
            "Iron Hook", new Item.Properties().stacksTo(1).defaultDurability(0), 
            1, 25, 2, 0.5, 50, 2
    );

    private final String name;
    private final Item.Properties properties;
    private final int count;
    private final double range;
    private final double speed;
    private final double length;
    private final int cooldown;
    private final double pullSpeed;

    HookTypeRegistry(String name, Item.Properties properties, int count, double range, double speed, double length, int cooldown, double pullSpeed) {
        this.name = name;
        this.properties = properties;
        this.count = count;
        this.range = range;
        this.speed = speed;
        this.length = length;
        this.cooldown = cooldown;
        this.pullSpeed = pullSpeed;
    }

    @Override
    public String Name() {
        return name;
    }

    @Override
    public Item.Properties ItemProperties() {
        return properties;
    }

    @Override
    public int Count() {
        return count;
    }

    @Override
    public double Range() {
        return range;
    }

    @Override
    public double Speed() {
        return speed;
    }

    @Override
    public double Length() {
        return length;
    }

    @Override
    public int Cooldown() {
        return cooldown;
    }

    @Override
    public double PullSpeed() {
        return pullSpeed;
    }
}

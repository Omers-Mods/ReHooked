package com.oe.rehooked.item.hooks.impl;

import com.oe.rehooked.item.hooks.def.BaseHookItem;
import com.oe.rehooked.item.hooks.def.HookProperties;

public class EnderHookItem extends BaseHookItem {
    public static final String NAME = "ender_hook";
    
    public EnderHookItem() {
        super(new HookProperties() {
            @Override
            public String DisplayName() {
                return "Ender Hook";
            }

            @Override
            public int Count() {
                return 1;
            }

            @Override
            public double Range() {
                return 64;
            }

            @Override
            public double Speed() {
                return Double.MAX_VALUE;
            }

            @Override
            public double PullSpeed() {
                return 45;
            }

            @Override
            public int Cooldown() {
                return 0;
            }
        });
    }
}

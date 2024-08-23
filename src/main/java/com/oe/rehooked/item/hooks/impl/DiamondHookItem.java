package com.oe.rehooked.item.hooks.impl;

import com.oe.rehooked.item.hooks.def.BaseHookItem;
import com.oe.rehooked.item.hooks.def.HookProperties;

public class DiamondHookItem extends BaseHookItem {
    public static final String NAME = "diamond_hook";

    public DiamondHookItem() {
        super(new HookProperties() {
            @Override
            public String DisplayName() {
                return "Diamond Hook";
            }

            @Override
            public int Count() {
                return 4;
            }

            @Override
            public double Range() {
                return 100;
            }

            @Override
            public double Speed() {
                return 20;
            }

            @Override
            public double PullSpeed() {
                return 20;
            }

            @Override
            public double Length() {
                return 0.5;
            }

            @Override
            public int Cooldown() {
                return 5;
            }
        });
    }
}

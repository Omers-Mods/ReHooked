package com.oe.rehooked.item.hooks.impl;

import com.oe.rehooked.item.hooks.def.BaseHookItem;
import com.oe.rehooked.item.hooks.def.HookProperties;

public class RedHookItem extends BaseHookItem {
    public static final String NAME = "red_hook";
    
    public RedHookItem() {
        super(new HookProperties() {
            @Override
            public String DisplayName() {
                return "Red Hook";
            }

            @Override
            public int Count() {
                return 4;
            }

            @Override
            public double Range() {
                return 24;
            }

            @Override
            public double Speed() {
                return 24;
            }

            @Override
            public double PullSpeed() {
                return 20;
            }

            @Override
            public int Cooldown() {
                return 0;
            }
        });
    }
}

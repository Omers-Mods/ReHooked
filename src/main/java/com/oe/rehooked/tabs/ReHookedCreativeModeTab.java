package com.oe.rehooked.tabs;

import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.item.ReHookedComponents;
import com.oe.rehooked.item.ReHookedItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ReHookedCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReHookedMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> HOOK_TAB = CREATIVE_MODE_TABS.register("hooks_tab", 
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ReHookedItems.DIAMOND_HOOK.get()))
                    .title(Component.translatable("creative.tab.hooks"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ReHookedItems.WOOD_HOOK.get());
                        pOutput.accept(ReHookedItems.IRON_HOOK.get());
                        pOutput.accept(ReHookedItems.DIAMOND_HOOK.get());
                        pOutput.accept(ReHookedItems.RED_HOOK.get());
                        pOutput.accept(ReHookedItems.BLAZE_HOOK.get());
                        pOutput.accept(ReHookedItems.ENDER_HOOK.get());
                        pOutput.accept(ReHookedComponents.WOOD_CHAIN.get());
                        pOutput.accept(ReHookedComponents.DIAMOND_CHAIN.get());
                        pOutput.accept(ReHookedComponents.BLAZE_CHAIN.get());
                        pOutput.accept(ReHookedComponents.ENDER_CHAIN.get());
                    })
                    .build());
    
    public static void Init(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

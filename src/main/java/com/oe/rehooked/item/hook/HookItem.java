package com.oe.rehooked.item.hook;

import com.oe.rehooked.client.KeyBindings;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.utils.HandlerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class HookItem extends Item implements ICurioItem {
    private final String hookType;
    
    public HookItem(String hookType) {
        this(new Item.Properties().stacksTo(1), hookType);
    }
    
    public HookItem(Item.Properties properties, String hookType) {
        super(properties);
        this.hookType = hookType;
    }

    public String getHookType() {
        return hookType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!(stack.getItem() instanceof HookItem)) return;
        tooltipComponents.add(Component.translatable("tooltip.rehooked:" + hookType + "_hook.info"));
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.rehooked.press_fire",
                    KeyBindings.getCombinedKeyName(KeyBindings.FIRE_HOOK_KEY)));
            tooltipComponents.add(Component.translatable("tooltip.rehooked.press_retract",
                    KeyBindings.getCombinedKeyName(KeyBindings.RETRACT_HOOK_KEY)));
            HookRegistry.getHookData(hookType).ifPresent(hookData -> {
                if (!hookData.isCreative()) {
                    tooltipComponents.add(Component.translatable("tooltip.rehooked.press_retract_all",
                            KeyBindings.getCombinedKeyName(KeyBindings.REMOVE_ALL_HOOKS_KEY)));
                }
            });
        }
        else {
            tooltipComponents.add(Component.translatable("tooltip.rehooked.press_shift_more_info")
                    .withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player owner)) return;
        HandlerHelper.getHookHandler(owner).ifPresent(ICommonPlayerHookHandler::onUnequip);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player owner)) return;
        HandlerHelper.getHookHandler(owner).ifPresent(ICommonPlayerHookHandler::onEquip);
    }
}

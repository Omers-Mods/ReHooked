package com.oe.rehooked.item.hook;

import com.oe.rehooked.capabilities.hooks.PlayerHookCapabilityProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.logging.Handler;

public class HookItem extends Item implements ICurioItem {
    public static final String HOOK_TYPE_TAG = "hook_type";
    
    private final String hookType;
    
    public HookItem(String hookType) {
        this(new Item.Properties().defaultDurability(0).stacksTo(1), hookType);
    }
    
    public HookItem(Item.Properties properties, String hookType) {
        super(properties);
        this.hookType = hookType;
    }

    public String getHookType() {
        return hookType;
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity maybePlayer = slotContext.entity();
        if (maybePlayer instanceof Player) {
            maybePlayer.getCapability(PlayerHookCapabilityProvider.PLAYER_HOOK_HANDLER).ifPresent(Handler -> {
                Handler.hookType("");
                Handler.removeAllHooks();
            });
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (!(pStack.getItem() instanceof HookItem)) return;
        pTooltipComponents.add(Component.translatable("tooltip.rehooked:" + ((HookItem) pStack.getItem())
                .hookType + "_hook.info"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}

package com.oe.rehooked.item.hooks.def;

import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public abstract class BaseHookItem extends Item implements ICurioItem {
    private final String displayName;
    private final int count;
    private final double range;
    private final double speed;
    private final double pullSpeed;
    private final double length;

    public BaseHookItem() {
        super(new Item.Properties().defaultDurability(0).stacksTo(1));
    }
    
    public BaseHookItem()

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (!(pStack.getItem() instanceof BaseHookItem)) return;
        pTooltipComponents.add(Component.translatable("tooltip.rehooked:" + ((BaseHookItem) pStack.getItem())
                .DisplayName().toLowerCase().replace(" ", "_") + ".info"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        BlockHitResult blockHit = VectorHelper.getLookingAt(player, ClipContext.Fluid.NONE, Range());
        BlockState blockState = level.getBlockState(blockHit.getBlockPos());
        if (blockState == Blocks.AIR.defaultBlockState()) {
            player.sendSystemMessage(Component.literal("No block in range!"));
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        }
        player.sendSystemMessage(Component.literal("Hit block " + blockState.getBlock().getName() + " at " + blockHit.getBlockPos().toShortString()));
        
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
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

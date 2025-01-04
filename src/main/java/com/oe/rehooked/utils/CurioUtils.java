package com.oe.rehooked.utils;

import com.oe.rehooked.item.hook.HookItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurioUtils {
    public static <T extends ICurioItem> Optional<List<ItemStack>> getCuriosOfType(Class<T> clazz, LivingEntity entity) {
        LazyOptional<ICuriosItemHandler> optInventory = CuriosApi.getCuriosInventory(entity);
        if (!optInventory.isPresent()) return Optional.empty();
        return optInventory.resolve().flatMap(inventory -> inventory.getStacksHandler("hook").map(slot -> {
            List<ItemStack> retList = new ArrayList<>();
            ItemStack stack;
            for (int i = 0; i < slot.getSlots(); i++) {
                 stack = slot.getStacks().getStackInSlot(i);
                 if (clazz.isAssignableFrom(stack.getItem().getClass()))
                     retList.add(stack);
            }
            return retList;
        }));
    }
    
    public static <T> Optional<T> getIfUnique(List<T> lst) {
        if (lst.size() == 1) return Optional.of(lst.get(0));
        return Optional.empty();
    }
    
    public static Optional<String> getHookType(Player owner) {
        return getCuriosOfType(HookItem.class, owner)
                .flatMap(CurioUtils::getIfUnique)
                .map(ItemStack::getItem)
                .map(item -> ((HookItem) item).getHookType());
    }
}

package com.oe.rehooked.handlers.hook.def;

import com.oe.rehooked.capabilities.hooks.ServerHookCapabilityProvider;
import com.oe.rehooked.data.HookData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public interface IServerPlayerHookHandler extends ICommonPlayerHookHandler {
    static LazyOptional<IServerPlayerHookHandler> FromPlayer(Player player) {
        return player.getCapability(ServerHookCapabilityProvider.SERVER_HOOK_HANDLER);
    }
    
    @Override
    default void jump() {
        update();
        getOwner().ifPresent(owner -> {
            getHookData().ifPresent(hookData -> {
                if (countPulling() > 0 && shouldMoveThisTick()) {
                    getDeltaVThisTick().add(0, Math.max(hookData.pullSpeed() / 2, 1.5), 0);
                    owner.jumpFromGround();
                    removeAllHooks();
                }
            });
        });
    }
}

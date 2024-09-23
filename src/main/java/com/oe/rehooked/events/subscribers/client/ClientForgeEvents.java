package com.oe.rehooked.events.subscribers.client;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.client.KeyBindings;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.entities.hook.HookEntity;
import com.oe.rehooked.handlers.hook.def.IClientPlayerHookHandler;
import com.oe.rehooked.handlers.hook.def.ICommonPlayerHookHandler;
import com.oe.rehooked.item.hook.HookItem;
import com.oe.rehooked.utils.CurioUtils;
import com.oe.rehooked.utils.HandlerHelper;
import com.oe.rehooked.utils.VectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = ReHookedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static long ticksSinceShot = 0;
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ticksSinceShot++;
        Optional<IClientPlayerHookHandler> optHandler = IClientPlayerHookHandler.FromPlayer(player).resolve();
        if (optHandler.isEmpty()) {
            LOGGER.debug("Player hook handler not found!");
            return;
        }
        IClientPlayerHookHandler handler = optHandler.get();
        if (KeyBindings.FIRE_HOOK_KEY.consumeClick() && ticksSinceShot > 5) {
            ticksSinceShot = 0;
            CurioUtils.GetCuriosOfType(HookItem.class, player).flatMap(CurioUtils::GetIfUnique).ifPresent(hookStack -> {
                Entity camera = Minecraft.getInstance().getCameraEntity();
                handler.shootFromRotation(camera.getXRot(), camera.getYRot());
            });
        }
        if (KeyBindings.RETRACT_HOOK_KEY.consumeClick()) {
            Optional<HookEntity> target = VectorHelper.acquireLookTarget(HookEntity.class, player, 0.5);
            target.ifPresent(handler::removeHook);
        }
        
        handler.setOwner(player).update();
        if (handler.shouldMoveThisTick()) {
            Vec3 deltaVThisTick = handler.getDeltaVThisTick();
            player.setDeltaMovement(deltaVThisTick);
        }
        if (KeyBindings.REMOVE_ALL_HOOKS_KEY.consumeClick() && !handler.getHookData().map(HookData::isCreative).orElse(false)) {
            handler.jump();
        }
    }
    
    @SubscribeEvent
    public static void onInputUpdate(MovementInputUpdateEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            IClientPlayerHookHandler.FromPlayer(player).ifPresent(handler -> {
                if (handler.countPulling() > 0) {
                    if (!player.getAbilities().mayfly && !player.getAbilities().flying) {
                        // prevent crouching while on hook which isn't creative
                        if (player.hasPose(Pose.CROUCHING)) {
                            event.getInput().shiftKeyDown = false;
                        }
                    }
                }
            });
        }
    }
    
    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (Minecraft.getInstance().player == null || 
                event.getEntity() == null || 
                event.getEntity().getUUID() != Minecraft.getInstance().player.getUUID()) return;
        
        // if the entity jumping is the client player
        HandlerHelper.getHookHandler(Minecraft.getInstance().player).ifPresent(ICommonPlayerHookHandler::jump);
    }
}

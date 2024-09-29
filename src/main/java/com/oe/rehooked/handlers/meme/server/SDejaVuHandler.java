package com.oe.rehooked.handlers.meme.server;

import com.mojang.logging.LogUtils;
import com.oe.rehooked.item.ReHookedItems;
import com.oe.rehooked.sound.ReHookedSounds;
import com.oe.rehooked.sound.ServerSoundManager;
import com.oe.rehooked.sound.info.impl.DejaVhukInfo;
import com.oe.rehooked.utils.HandlerHelper;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.UUID;

public class SDejaVuHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final long TIME_BETWEEN_CHECKS = 10;
    private static final long TICKS_ABOVE_THRESHOLD_TO_START = 2;
    private static long timeSinceLastCheck = 0;
    private static long ticksAboveThreshold = 0;
    
    private UUID soundId = null;
    private ServerPlayer owner;
    
    public SDejaVuHandler setOwner(ServerPlayer owner) {
        this.owner = owner;
        return this;
    }
    
    public void tick() {
        long prevTickCount = ticksAboveThreshold;
        timeSinceLastCheck++;
        if (owner == null || timeSinceLastCheck < TIME_BETWEEN_CHECKS) {
            return;
        }
        if (!ServerSoundManager.isSoundActive(owner.level(), soundId)) soundId = null;
        timeSinceLastCheck = 0;
        HandlerHelper.getHookHandler(owner).ifPresent(handler -> {
            handler.getHookData().ifPresent(hookData -> {
                if (hookData.type().equals(ReHookedItems.DEJA_VU)) {
                    // player has a hook that activates this
                    if (soundId == null || !ServerSoundManager.isSoundActive(owner.level(), soundId)) {
                        // sound not active
                        if (handler.getMomentum() != null || (handler.shouldMoveThisTick() && handler.actualPlayerPositionChange().length() >= DejaVhukInfo.SPEED_THRESHOLD)) {
                            // should activate sound
                            if (soundId == null || ServerSoundManager.isSoundActive(owner.level(), soundId)) {
                                ticksAboveThreshold++;
                            }
                        }
                    }
                }
            });
        });
        if (ticksAboveThreshold == prevTickCount) {
            ticksAboveThreshold = 0;
        }
        else if (ticksAboveThreshold >= TICKS_ABOVE_THRESHOLD_TO_START) {
            // should add sound to manager
            soundId = ServerSoundManager.addSound(owner.serverLevel(), 
                    new DejaVhukInfo(
                            owner.getId(),
                            ReHookedSounds.DEJA_VU,
                            16,
                            1
                    )
            );
            ticksAboveThreshold = 0;
        }
    }
}

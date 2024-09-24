package com.oe.rehooked.entities.hook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.oe.rehooked.data.HookData;
import com.oe.rehooked.data.HookRegistry;
import com.oe.rehooked.entities.layers.ReHookedModelLayers;
import com.oe.rehooked.utils.PositionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HookEntityRenderer extends EntityRenderer<HookEntity> {
    protected EntityModel<HookEntity> model;
    protected EntityRendererProvider.Context pContext;
    protected float lastDelta;
    
    public HookEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        model = new HookEntityModel(pContext.bakeLayer(ReHookedModelLayers.HOOK_PROJECTILE_LAYER));
        this.pContext = pContext;
        lastDelta = 0;
    }

    @Override
    public void render(HookEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.getHookType().isEmpty()) return;
        handleHook(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        if (pEntity.hasChain() && pEntity.getOwner() != null) handleChain(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    private void handleHook(HookEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        Vec3 dV = pEntity.getDeltaMovement();
        if (!dV.equals(Vec3.ZERO)) {
            pEntity.lookAt(EntityAnchorArgument.Anchor.EYES, pEntity.getEyePosition().add(dV));
        }
        else {
            pEntity.getHitPos()
                    .ifPresent(blockPos -> pEntity.lookAt(EntityAnchorArgument.Anchor.EYES, blockPos.getCenter()));
        }
        pPoseStack.mulPose(Axis.YP.rotationDegrees(270f - pEntity.getYRot()));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(90f - pEntity.getXRot()));
        pPoseStack.scale(0.4f, 0.4f, 0.4f);
        pPoseStack.translate(0, -1, 0);
        this.model.renderToBuffer(pPoseStack, pBuffer.getBuffer(model.renderType(getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
    }
    
    private void handleChain(HookEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        // get relevant positions
        Entity owner = pEntity.getOwner();
        Vec3 waistPos = PositionHelper.getWaistPosition(owner);
        Vec3 hookPos = pEntity.position();
        Vec3 playerToHook = waistPos.vectorTo(hookPos);
        Vec3 normal = playerToHook.normalize();
        // rotate
        pPoseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI - (float) Mth.atan2(normal.z, normal.x)));
        pPoseStack.mulPose(Axis.XP.rotation((float) Math.acos(normal.y) - Mth.PI));
        pPoseStack.translate(-0.5, 0.0, -0.5);
        // add chains
        float distance = (float) playerToHook.length();
        pEntity.getHookType().flatMap(HookRegistry::getHookData).ifPresent(hookData -> {
            BlockState chain = Blocks.CHAIN.defaultBlockState();
            for (int i = 0; i < (int) distance; i++) {
                pContext.getBlockRenderDispatcher().renderSingleBlock(chain, pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);
                pPoseStack.translate(0, 1, 0);
            }
            float delta = distance - (int) distance;
            if (lastDelta == 0) lastDelta = delta;
            if (Math.abs(delta - lastDelta) < 0.3f) delta = lastDelta;
            pPoseStack.scale(1, delta, 1);
            lastDelta = delta;
            pContext.getBlockRenderDispatcher().renderSingleBlock(chain, pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);
            pPoseStack.popPose();
        });
    }
    
    @Override
    public ResourceLocation getTextureLocation(HookEntity pEntity) {
        return pEntity.getHookType().flatMap(HookRegistry::getHookData).map(HookData::texture).orElseGet(null);
    }
}

package com.oe.rehooked.entities.hook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.oe.rehooked.data.ChainRegistry;
import com.oe.rehooked.data.HookData;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
    public boolean shouldRender(HookEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        if (pLivingEntity.isRemoved() || pLivingEntity.getHookType().isEmpty() || pLivingEntity.getState().equals(HookEntity.State.DONE)) return false;
        if (pLivingEntity.tryGetOwnerFromCachedId() != null) {
            Player owner = pLivingEntity.tryGetOwnerFromCachedId();
            if (Minecraft.getInstance().player != null && owner.getUUID().equals(Minecraft.getInstance().player.getUUID()))
                return true;
            return pCamera.isVisible(owner.getBoundingBox()) || super.shouldRender(pLivingEntity, pCamera, pCamX, pCamY, pCamZ);
        }
        return false;
    }

    @Override
    public void render(HookEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        handleHook(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, 15728880);
        if (pEntity.hasChain() && pEntity.tryGetOwnerFromCachedId() != null) handleChain(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, 15728880);
    }

    private void handleHook(HookEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        Vec3 direction = new Vec3(pEntity.getShotDirection());
        Vec3 lookAt = pEntity.getEyePosition().add(direction);
        pEntity.lookAt(EntityAnchorArgument.Anchor.EYES, lookAt);
        AABB box = pEntity.getBoundingBox();
        pPoseStack.scale((float) box.getXsize(), (float) box.getYsize(), (float) box.getZsize());
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180f));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntity.getYRot() + 270f));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(pEntity.getXRot() + 90f));
        pPoseStack.translate(0, -1, 0);
        this.model.renderToBuffer(pPoseStack, pBuffer.getBuffer(model.renderType(getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY);
        pPoseStack.popPose();
    }
    
    private void handleChain(HookEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        // get relevant positions
        Entity owner = pEntity.tryGetOwnerFromCachedId();
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
        BlockState chain = ChainRegistry.getChain(pEntity.getHookType()).defaultBlockState();
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
    }
    
    @Override
    public ResourceLocation getTextureLocation(HookEntity pEntity) {
        return pEntity.getHookData().map(HookData::texture).orElseGet(null);
    }
}

package com.oe.rehooked.entities.hook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.entities.layers.ReHookedModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HookEntityRenderer extends EntityRenderer<HookEntity> {
    public static final ResourceLocation TEXTURE = 
            new ResourceLocation(ReHookedMod.MOD_ID, "textures/hook/hook_test/hook_test.png");
    protected HookEntityModel model;
    
    public HookEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        model = new HookEntityModel(pContext.bakeLayer(ReHookedModelLayers.HOOK_PROJECTILE_LAYER));
        this.shadowRadius = 0.25f;
    }

    public void render(HookEntity entity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, entity.yRotO, entity.getYRot() - 90.0f)));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTick, entity.xRotO, entity.getXRot() + 90.0f)));
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(pBuffer, this.model.renderType(this.getTextureLocation(entity)), false, false);
        
        this.model.renderToBuffer(pPoseStack, vertexConsumer,pPackedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        pPoseStack.popPose();
        super.render(entity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
    
    @Override
    public ResourceLocation getTextureLocation(HookEntity pEntity) {
        return TEXTURE;
    }
}

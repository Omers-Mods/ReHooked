package com.oe.rehooked.entities.test;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.oe.rehooked.ReHookedMod;
import com.oe.rehooked.entities.layers.ReHookedModelLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TestCubeRenderer extends EntityRenderer<TestCubeEntity> {
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ReHookedMod.MOD_ID, "textures/test/direction_cube/test_cube.png");
    protected EntityRendererProvider.Context pContext;
    protected EntityModel<TestCubeEntity> model;
    protected float lastDelta = 0;

    public TestCubeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.pContext = pContext; 
        model = new TestCubeModel(pContext.bakeLayer(ReHookedModelLayers.TEST_CUBE_LAYER));
    }

    @Override
    public void render(TestCubeEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        handleCube(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        handleChain(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    private void handleCube(TestCubeEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-Minecraft.getInstance().player.getYHeadRot() + 90f));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Minecraft.getInstance().player.getXRot()));
        pPoseStack.translate(0, -0.5, 0);
        this.model.renderToBuffer(pPoseStack, pBuffer.getBuffer(model.renderType(getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY);
        pPoseStack.popPose();
    }

    @Override
    public boolean shouldRender(TestCubeEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }

    private void handleChain(TestCubeEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        LocalPlayer player = Minecraft.getInstance().player;
        // get relevant positions
        Vec3 waistPos = player != null ? player.position().add(0, player.getEyeHeight() / 1.5, 0) : pEntity.position();
        Vec3 cubePos = pEntity.position();
        Vec3 playerToCube = waistPos.vectorTo(cubePos);
        Vec3 normal = playerToCube.normalize();
        // rotate
        pPoseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI - (float) Mth.atan2(normal.z, normal.x)));
        pPoseStack.mulPose(Axis.XP.rotation((float) Math.acos(normal.y) - Mth.PI));
        pPoseStack.translate(-0.5, 0.0, -0.75);
        // add chains
        float distance = (float) playerToCube.length();
        BlockState chain = Blocks.CHAIN.defaultBlockState();
        for (int i = 0; i < (int) distance; i++) {
            pContext.getBlockRenderDispatcher().renderSingleBlock(chain, pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);
            pPoseStack.translate(0, 1, 0);
        }
        float delta = distance - (int) distance;
        if (lastDelta - delta > 0.5f || lastDelta == 0) lastDelta = delta;
        delta = Mth.lerp(pPartialTick, lastDelta, delta);
        pPoseStack.scale(1, delta, 1);
        lastDelta = delta;
        pContext.getBlockRenderDispatcher().renderSingleBlock(chain, pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);
        pPoseStack.popPose();
    }
    
    @Override
    public ResourceLocation getTextureLocation(TestCubeEntity pEntity) {
        return TEXTURE;
    }
}

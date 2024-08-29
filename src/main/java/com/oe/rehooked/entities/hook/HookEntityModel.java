package com.oe.rehooked.entities.hook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.oe.rehooked.ReHookedMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class HookEntityModel<T extends HookEntity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = 
            new ModelLayerLocation(new ResourceLocation(ReHookedMod.MOD_ID, "hookentitymodel"), "main");
    private final ModelPart bone;

    public HookEntityModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-1.3F, -5.0F, -0.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3F, 19.0F, 0.5F, 3.1416F, 0.0F, 0.0F));

        PartDefinition W_r1 = bone.addOrReplaceChild("W_r1", CubeListBuilder.create().texOffs(8, 6).addBox(-0.5F, -1.1F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.8F, 0.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition E_r1 = bone.addOrReplaceChild("E_r1", CubeListBuilder.create().texOffs(4, 0).addBox(-0.5F, -2.9F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.9F, -2.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition S_r1 = bone.addOrReplaceChild("S_r1", CubeListBuilder.create().texOffs(4, 6).addBox(-0.5F, -2.9F, 2.2F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8F, -0.7F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition N_r1 = bone.addOrReplaceChild("N_r1", CubeListBuilder.create().texOffs(8, 0).addBox(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8F, -2.9F, -1.7F, -0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(HookEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
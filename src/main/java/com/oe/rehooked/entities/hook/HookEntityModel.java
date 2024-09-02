package com.oe.rehooked.entities.hook;// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class HookEntityModel extends EntityModel<HookEntity> {
	private final ModelPart hook;

	public HookEntityModel(ModelPart root) {
		this.hook = root.getChild("hook");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition hook = partdefinition.addOrReplaceChild("hook", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		hook.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -7.0F, -7.0F, 14.0F, 7.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition tooth1 = hook.addOrReplaceChild("tooth1", CubeListBuilder.create().texOffs(35, 43).addBox(-7.0F, -13.0F, 3.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition tooth1_2 = tooth1.addOrReplaceChild("tooth1_2", CubeListBuilder.create(), PartPose.offset(-4.5F, -15.0F, 4.5F));

		tooth1_2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(52, 55).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.2618F, 0.0F, 0.2618F));

		PartDefinition tooth2 = hook.addOrReplaceChild("tooth2", CubeListBuilder.create().texOffs(35, 32).addBox(-7.0F, -13.0F, 2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -9.0F));

		PartDefinition tooth2_2 = tooth2.addOrReplaceChild("tooth2_2", CubeListBuilder.create(), PartPose.offset(-4.5F, -15.0F, 3.5F));

		tooth2_2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(52, 35).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 1.0F, -0.2618F, 0.0F, 0.2618F));

		tooth2_2.addOrReplaceChild("tooth2_3", CubeListBuilder.create(), PartPose.offset(10.0F, 0.0F, 0.0F));

		PartDefinition tooth3 = hook.addOrReplaceChild("tooth3", CubeListBuilder.create().texOffs(35, 54).addBox(-6.0F, -13.0F, 3.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, 0.0F, 0.0F));

		PartDefinition tooth3_2 = tooth3.addOrReplaceChild("tooth3_2", CubeListBuilder.create(), PartPose.offset(-2.5F, -15.0F, 4.5F));

		tooth3_2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(52, 45).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -0.5F, 0.0F, 0.2618F, 0.0F, -0.2618F));

		PartDefinition tooth4 = hook.addOrReplaceChild("tooth4", CubeListBuilder.create().texOffs(18, 54).addBox(-6.0F, -13.0F, 2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, 0.0F, -9.0F));

		PartDefinition tooth4_2 = tooth4.addOrReplaceChild("tooth4_2", CubeListBuilder.create(), PartPose.offset(-3.5F, -15.0F, 3.5F));

		tooth4_2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(52, 25).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -0.5F, 1.0F, -0.2618F, 0.0F, -0.2618F));

		tooth4_2.addOrReplaceChild("tooth4_3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(HookEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		hook.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
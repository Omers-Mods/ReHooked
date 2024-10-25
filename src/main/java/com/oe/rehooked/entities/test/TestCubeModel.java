package com.oe.rehooked.entities.test;// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class TestCubeModel extends EntityModel<TestCubeEntity> {
	private final ModelPart directionCube;

	public TestCubeModel(ModelPart root) {
		this.directionCube = root.getChild("directionCube");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition directionCube = partdefinition.addOrReplaceChild("directionCube", CubeListBuilder.create().texOffs(0, 18).addBox(-8.0F, 7.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

		PartDefinition negX_r1 = directionCube.addOrReplaceChild("negX_r1", CubeListBuilder.create().texOffs(49, 0).addBox(-7.0F, 7.0F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(49, 18).addBox(-7.0F, -8.0F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -1.5708F));

		PartDefinition posZ_r1 = directionCube.addOrReplaceChild("posZ_r1", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, 7.0F, -7.0F, 16.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition negZ_r1 = directionCube.addOrReplaceChild("negZ_r1", CubeListBuilder.create().texOffs(47, 38).addBox(-8.0F, 7.0F, -7.0F, 16.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(TestCubeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
		directionCube.render(poseStack, vertexConsumer, i, i1, i2);
	}
}
package com.oe.rehooked.entities.hook;// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class THookEntityModel<T extends HookEntity> extends EntityModel<T> {
	private final ModelPart Hook;
	private final ModelPart Shaft;
	private final ModelPart Tip;
	private final ModelPart Tooth1;
	private final ModelPart Tooth2;
	private final ModelPart Tooth3;
	private final ModelPart Tooth4;

	public THookEntityModel(ModelPart root) {
		this.Hook = root.getChild("hook");
		this.Shaft = Hook.getChild("shaft");
		this.Tip = Hook.getChild("tip");
		this.Tooth1 = Tip.getChild("tooth1");
		this.Tooth2 = Tip.getChild("tooth2");
		this.Tooth3 = Tip.getChild("tooth3");
		this.Tooth4 = Tip.getChild("tooth4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Hook = partdefinition.addOrReplaceChild("hook", CubeListBuilder.create(), PartPose.offset(0.0F, 31.0F, 0.0F));

		Hook.addOrReplaceChild("shaft", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -7.5F, -0.5F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.5F, 0.0F));

		PartDefinition Tip = Hook.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(14, 17).addBox(-0.5F, -3.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.5F, 0.0F));

		Tip.addOrReplaceChild("tooth1", CubeListBuilder.create().texOffs(9, 16).addBox(1.0F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(13, 13).addBox(0.0F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 10).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -2.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition Tooth2 = Tip.addOrReplaceChild("tooth2", CubeListBuilder.create().texOffs(15, 0).addBox(-0.5F, -2.5F, -2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 2.5F, 0.0F, 0.0F, -3.1416F));

		Tooth2.addOrReplaceChild("t2_3_r1", CubeListBuilder.create().texOffs(9, 9).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 13).addBox(0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.5F, 0.0F, 1.5708F, 0.0F));

		PartDefinition Tooth3 = Tip.addOrReplaceChild("tooth3", CubeListBuilder.create().texOffs(0, 15).addBox(-0.5F, -2.5F, 1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -2.5F, 0.0F, 0.0F, -3.1416F));

		Tooth3.addOrReplaceChild("t3_3_r1", CubeListBuilder.create().texOffs(5, 0).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, -0.5F, 0.0F, -1.5708F, 0.0F));

		Tooth3.addOrReplaceChild("t3_2_r1", CubeListBuilder.create().texOffs(10, 4).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 0.0F, -1.5708F, 0.0F));

		PartDefinition Tooth4 = Tip.addOrReplaceChild("tooth4", CubeListBuilder.create().texOffs(14, 7).addBox(-2.0F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -2.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		Tooth4.addOrReplaceChild("t4_3_r1", CubeListBuilder.create().texOffs(5, 5).addBox(-1.0F, -0.5F, -0.25F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(10, 0).addBox(0.0F, -1.5F, -0.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.25F, 0.0F, 3.1416F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(HookEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Hook.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
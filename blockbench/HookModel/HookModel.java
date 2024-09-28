// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class HookModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "hookmodel"), "main");
	private final ModelPart hook;
	private final ModelPart body;
	private final ModelPart tooth1;
	private final ModelPart tooth1_2;
	private final ModelPart tooth2;
	private final ModelPart tooth2_2;
	private final ModelPart tooth2_3;
	private final ModelPart tooth3;
	private final ModelPart tooth3_2;
	private final ModelPart tooth4;
	private final ModelPart tooth4_2;
	private final ModelPart tooth4_3;

	public HookModel(ModelPart root) {
		this.hook = root.getChild("hook");
		this.body = root.getChild("body");
		this.tooth1 = root.getChild("tooth1");
		this.tooth1_2 = root.getChild("tooth1_2");
		this.tooth2 = root.getChild("tooth2");
		this.tooth2_2 = root.getChild("tooth2_2");
		this.tooth2_3 = root.getChild("tooth2_3");
		this.tooth3 = root.getChild("tooth3");
		this.tooth3_2 = root.getChild("tooth3_2");
		this.tooth4 = root.getChild("tooth4");
		this.tooth4_2 = root.getChild("tooth4_2");
		this.tooth4_3 = root.getChild("tooth4_3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition hook = partdefinition.addOrReplaceChild("hook", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = hook.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -7.0F, -7.0F, 14.0F, 7.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition tooth1 = hook.addOrReplaceChild("tooth1", CubeListBuilder.create().texOffs(35, 43).addBox(-7.0F, -13.0F, 3.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition tooth1_2 = tooth1.addOrReplaceChild("tooth1_2", CubeListBuilder.create(), PartPose.offset(-4.5F, -15.0F, 4.5F));

		PartDefinition cube_r1 = tooth1_2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(52, 55).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.2618F, 0.0F, 0.2618F));

		PartDefinition tooth2 = hook.addOrReplaceChild("tooth2", CubeListBuilder.create().texOffs(35, 32).addBox(-7.0F, -13.0F, 2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -9.0F));

		PartDefinition tooth2_2 = tooth2.addOrReplaceChild("tooth2_2", CubeListBuilder.create(), PartPose.offset(-4.5F, -15.0F, 3.5F));

		PartDefinition cube_r2 = tooth2_2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(52, 35).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 1.0F, -0.2618F, 0.0F, 0.2618F));

		PartDefinition tooth2_3 = tooth2_2.addOrReplaceChild("tooth2_3", CubeListBuilder.create(), PartPose.offset(10.0F, 0.0F, 0.0F));

		PartDefinition tooth3 = hook.addOrReplaceChild("tooth3", CubeListBuilder.create().texOffs(35, 54).addBox(-6.0F, -13.0F, 3.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, 0.0F, 0.0F));

		PartDefinition tooth3_2 = tooth3.addOrReplaceChild("tooth3_2", CubeListBuilder.create(), PartPose.offset(-2.5F, -15.0F, 4.5F));

		PartDefinition cube_r3 = tooth3_2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(52, 45).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -0.5F, 0.0F, 0.2618F, 0.0F, -0.2618F));

		PartDefinition tooth4 = hook.addOrReplaceChild("tooth4", CubeListBuilder.create().texOffs(18, 54).addBox(-6.0F, -13.0F, 2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, 0.0F, -9.0F));

		PartDefinition tooth4_2 = tooth4.addOrReplaceChild("tooth4_2", CubeListBuilder.create(), PartPose.offset(-3.5F, -15.0F, 3.5F));

		PartDefinition cube_r4 = tooth4_2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(52, 25).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -0.5F, 1.0F, -0.2618F, 0.0F, -0.2618F));

		PartDefinition tooth4_3 = tooth4_2.addOrReplaceChild("tooth4_3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		hook.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
package com.moondrip.client;

import com.moondrip.entity.SolarCreeperEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

// Modelo hecho en Blockbench (Fabric / Yarn), adaptado a SinglePartEntityModel para 1.21
public class SolarCreeperModel extends SinglePartEntityModel<SolarCreeperEntity> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart leg0;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart leg3;

	public SolarCreeperModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.leg0 = root.getChild("leg0");
		this.leg1 = root.getChild("leg1");
		this.leg2 = root.getChild("leg2");
		this.leg3 = root.getChild("leg3");
	}

	// Modelo normal (sin inflar)
	public static TexturedModelData getTexturedModelData() {
		return getTexturedModelData(0.0F);
	}

	// Con "dilation" mayor a 0 el modelo queda mas grande: se usa para el aura que rodea al creeper
	public static TexturedModelData getTexturedModelData(float dilation) {
		Dilation d = new Dilation(dilation);
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, d), ModelTransform.pivot(0.0F, 6.0F, 0.0F));
		modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, d), ModelTransform.pivot(0.0F, 6.0F, 0.0F));
		modelPartData.addChild("leg0", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, d), ModelTransform.pivot(2.0F, 18.0F, 4.0F));
		modelPartData.addChild("leg1", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, d), ModelTransform.pivot(-2.0F, 18.0F, 4.0F));
		modelPartData.addChild("leg2", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, d), ModelTransform.pivot(2.0F, 18.0F, -4.0F));
		modelPartData.addChild("leg3", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, d), ModelTransform.pivot(-2.0F, 18.0F, -4.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(SolarCreeperEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.yaw = headYaw * ((float) Math.PI / 180F);
		this.head.pitch = headPitch * ((float) Math.PI / 180F);
		float a = limbAngle * 0.6662F;
		float amp = 1.4F * limbDistance;
		this.leg0.pitch = MathHelper.cos(a + (float) Math.PI) * amp;
		this.leg1.pitch = MathHelper.cos(a) * amp;
		this.leg2.pitch = MathHelper.cos(a) * amp;
		this.leg3.pitch = MathHelper.cos(a + (float) Math.PI) * amp;
	}
}

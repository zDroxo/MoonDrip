package com.moondrip.client;

import com.moondrip.entity.PezLinternaEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

// Modelo hecho en Blockbench (pez_linterna_v4.bbmodel), pasado a codigo con nombres Yarn.
// El frente del pez es -Z.
public class PezLinternaModel extends SinglePartEntityModel<PezLinternaEntity> {
	private final ModelPart root;
	private final ModelPart tail;
	private final ModelPart jaw;
	private final ModelPart finLeft;
	private final ModelPart finRight;

	public PezLinternaModel(ModelPart root) {
		this.root = root;
		this.tail = root.getChild("tail");
		this.jaw = root.getChild("jaw");
		this.finLeft = root.getChild("fin_left");
		this.finRight = root.getChild("fin_right");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();

		root.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -12.0F, -7.0F, 8.0F, 8.0F, 14.0F), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		root.addChild("tail", ModelPartBuilder.create().uv(0, 22).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 6.0F), ModelTransform.pivot(0.0F, 16.0F, 7.0F));
		root.addChild("antenna", ModelPartBuilder.create()
				.uv(44, 8).cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F)
				.uv(44, 0).cuboid(-2.0F, -8.0F, -2.0F, 4.0F, 4.0F, 4.0F), ModelTransform.pivot(0.0F, 12.0F, -4.0F));
		root.addChild("jaw", ModelPartBuilder.create().uv(20, 22).cuboid(-3.0F, 0.0F, -4.0F, 6.0F, 2.0F, 5.0F), ModelTransform.pivot(0.0F, 19.0F, -6.0F));
		root.addChild("snout", ModelPartBuilder.create().uv(44, 24).cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F, 18.0F, -7.0F));
		root.addChild("dorsal_fin", ModelPartBuilder.create().uv(44, 14).cuboid(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 6.0F), ModelTransform.pivot(0.0F, 12.0F, 3.0F));
		root.addChild("fin_left", ModelPartBuilder.create().uv(0, 32).cuboid(0.0F, 0.0F, -1.0F, 3.0F, 1.0F, 4.0F), ModelTransform.pivot(4.0F, 18.0F, -2.0F));
		root.addChild("fin_right", ModelPartBuilder.create().uv(14, 32).cuboid(-3.0F, 0.0F, -1.0F, 3.0F, 1.0F, 4.0F), ModelTransform.pivot(-4.0F, 18.0F, -2.0F));

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(PezLinternaEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		// Cola: se mueve de lado a lado todo el tiempo (mas rapido si nada rapido)
		this.tail.yaw = MathHelper.sin(animationProgress * 0.3F) * 0.4F;
		// Mandibula: abre y cierra suave (pitch positivo = abre)
		this.jaw.pitch = 0.1F + MathHelper.sin(animationProgress * 0.15F) * 0.1F;
		// Aletas: aletean al reves una de otra
		float flap = MathHelper.sin(animationProgress * 0.25F) * 0.35F;
		this.finLeft.roll = flap;
		this.finRight.roll = -flap;
	}
}

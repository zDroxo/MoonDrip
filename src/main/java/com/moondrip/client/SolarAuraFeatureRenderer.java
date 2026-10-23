package com.moondrip.client;

import com.moondrip.MoonDrip;
import com.moondrip.entity.SolarCreeperEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SolarAuraFeatureRenderer extends FeatureRenderer<SolarCreeperEntity, SolarCreeperModel> {

	// Que tan grande es el aura (en pixeles de modelo). Mas alto = aura mas gorda.
	public static final float AURA_DILATION = 1.5F;
	// Velocidad con la que se mueve el aura
	private static final float SCROLL_SPEED = 0.012F;

	private static final Identifier AURA_TEXTURE = Identifier.of(MoonDrip.MOD_ID, "textures/entity/solar_aura.png");

	private final SolarCreeperModel auraModel;

	public SolarAuraFeatureRenderer(FeatureRendererContext<SolarCreeperEntity, SolarCreeperModel> context, EntityModelLoader loader) {
		super(context);
		this.auraModel = new SolarCreeperModel(loader.getModelPart(ModModelLayers.SOLAR_CREEPER_AURA));
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, SolarCreeperEntity entity,
					   float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		float t = (float) entity.age + tickDelta;
		float u = (t * SCROLL_SPEED) % 1.0F;
		float v = (t * SCROLL_SPEED * 0.5F) % 1.0F;

		this.auraModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(AURA_TEXTURE, u, v));
		this.auraModel.render(matrices, consumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0xFF808080);
	}
}

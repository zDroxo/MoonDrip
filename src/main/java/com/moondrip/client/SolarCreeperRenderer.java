package com.moondrip.client;

import com.moondrip.MoonDrip;
import com.moondrip.entity.SolarCreeperEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SolarCreeperRenderer extends MobEntityRenderer<SolarCreeperEntity, SolarCreeperModel> {
	private static final Identifier TEXTURE = Identifier.of(MoonDrip.MOD_ID, "textures/entity/solar_creeper.png");

	public SolarCreeperRenderer(EntityRendererFactory.Context context) {
		super(context, new SolarCreeperModel(context.getPart(ModModelLayers.SOLAR_CREEPER)), 0.5F);
	}

	@Override
	public Identifier getTexture(SolarCreeperEntity entity) {
		return TEXTURE;
	}

	// Se hincha antes de explotar, igual que el creeper normal
	@Override
	protected void scale(SolarCreeperEntity entity, MatrixStack matrices, float amount) {
		float f = entity.getClientFuseTime(amount);
		float g = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		f *= f;
		f *= f;
		float h = (1.0F + f * 0.4F) * g;
		float i = (1.0F + f * 0.1F) / g;
		matrices.scale(h, i, h);
	}
}

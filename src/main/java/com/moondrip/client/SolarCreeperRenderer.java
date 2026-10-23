package com.moondrip.client;

import com.moondrip.MoonDrip;
import com.moondrip.entity.SolarCreeperEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SolarCreeperRenderer extends MobEntityRenderer<SolarCreeperEntity, SolarCreeperModel> {

	// Textura animada: 4 cuadros (solar_creeper_0.png ... solar_creeper_3.png)
	private static final int FRAME_COUNT = 4;
	// Cada cuadro dura esta cantidad de ticks (20 ticks = 1 segundo). Subilo para que vaya mas lento.
	private static final int FRAME_TICKS = 4;

	private static final Identifier[] TEXTURES = new Identifier[FRAME_COUNT];

	static {
		for (int i = 0; i < FRAME_COUNT; i++) {
			TEXTURES[i] = Identifier.of(MoonDrip.MOD_ID, "textures/entity/solar_creeper_" + i + ".png");
		}
	}

	public SolarCreeperRenderer(EntityRendererFactory.Context context) {
		super(context, new SolarCreeperModel(context.getPart(ModModelLayers.SOLAR_CREEPER)), 0.5F);
		// Aura solar alrededor del creeper
		this.addFeature(new SolarAuraFeatureRenderer(this, context.getModelLoader()));
	}

	@Override
	public Identifier getTexture(SolarCreeperEntity entity) {
		int frame = (entity.age / FRAME_TICKS) % FRAME_COUNT;
		return TEXTURES[frame];
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

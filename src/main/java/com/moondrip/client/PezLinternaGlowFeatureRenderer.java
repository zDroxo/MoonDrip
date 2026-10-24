package com.moondrip.client;

import com.moondrip.MoonDrip;
import com.moondrip.entity.PezLinternaEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;

// Hace que la linterna brille siempre, incluso de noche (igual que los ojos de la arana).
// Usa pez_linterna_glow.png: es la misma textura pero con SOLO la linterna pintada (lo demas transparente).
public class PezLinternaGlowFeatureRenderer extends EyesFeatureRenderer<PezLinternaEntity, PezLinternaModel> {
	private static final RenderLayer GLOW = RenderLayer.getEyes(Identifier.of(MoonDrip.MOD_ID, "textures/entity/pez_linterna_glow.png"));

	public PezLinternaGlowFeatureRenderer(FeatureRendererContext<PezLinternaEntity, PezLinternaModel> context) {
		super(context);
	}

	@Override
	public RenderLayer getEyesTexture() {
		return GLOW;
	}
}

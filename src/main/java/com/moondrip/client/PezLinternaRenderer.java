package com.moondrip.client;

import com.moondrip.MoonDrip;
import com.moondrip.entity.PezLinternaEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class PezLinternaRenderer extends MobEntityRenderer<PezLinternaEntity, PezLinternaModel> {
	private static final Identifier TEXTURE = Identifier.of(MoonDrip.MOD_ID, "textures/entity/pez_linterna.png");

	public PezLinternaRenderer(EntityRendererFactory.Context context) {
		super(context, new PezLinternaModel(context.getPart(ModModelLayers.PEZ_LINTERNA)), 0.4F);
		// La linterna brilla de noche
		this.addFeature(new PezLinternaGlowFeatureRenderer(this));
	}

	@Override
	public Identifier getTexture(PezLinternaEntity entity) {
		return TEXTURE;
	}
}

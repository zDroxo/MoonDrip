package com.moondrip.client;

import com.moondrip.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class MoonDripClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SOLAR_CREEPER, SolarCreeperModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SOLAR_CREEPER_AURA, () -> SolarCreeperModel.getTexturedModelData(SolarAuraFeatureRenderer.AURA_DILATION));
		EntityRendererRegistry.register(ModEntities.SOLAR_CREEPER, SolarCreeperRenderer::new);
	}
}

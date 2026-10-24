package com.moondrip.client;

import com.moondrip.MoonDrip;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
	public static final EntityModelLayer SOLAR_CREEPER = new EntityModelLayer(Identifier.of(MoonDrip.MOD_ID, "solar_creeper"), "main");
	// Version inflada del modelo: es la capa del aura
	public static final EntityModelLayer SOLAR_CREEPER_AURA = new EntityModelLayer(Identifier.of(MoonDrip.MOD_ID, "solar_creeper"), "aura");

	public static final EntityModelLayer PEZ_LINTERNA = new EntityModelLayer(Identifier.of(MoonDrip.MOD_ID, "pez_linterna"), "main");
}

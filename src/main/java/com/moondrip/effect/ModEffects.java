package com.moondrip.effect;

import com.moondrip.MoonDrip;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {
	public static final RegistryEntry<StatusEffect> HIPNOSIS = Registry.registerReference(
			Registries.STATUS_EFFECT,
			Identifier.of(MoonDrip.MOD_ID, "hipnosis"),
			new HipnosisEffect()
	);

	// Se llama una vez al iniciar el mod para que se registren los efectos
	public static void register() {
	}
}

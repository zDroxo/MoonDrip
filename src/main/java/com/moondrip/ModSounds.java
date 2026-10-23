package com.moondrip;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

	// Pitido que suena cuando explota el Creeper Solar (archivo: sounds/solar_flash_ring.ogg)
	public static final SoundEvent SOLAR_FLASH_RING = register("solar_flash_ring");

	private static SoundEvent register(String name) {
		Identifier id = Identifier.of(MoonDrip.MOD_ID, name);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

	// Se llama al arrancar el mod para que los sonidos queden registrados
	public static void register() {
	}
}

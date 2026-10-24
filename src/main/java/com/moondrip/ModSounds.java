package com.moondrip;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

	// Pitido que suena cuando explota el Creeper Solar (archivo: sounds/solar_flash_ring.ogg)
	public static final SoundEvent SOLAR_FLASH_RING = register("solar_flash_ring");

	// Sonidos del Pez Linterna (archivos en sounds/ con el mismo nombre + .ogg)
	public static final SoundEvent PEZ_LINTERNA_AMBIENT1 = register("pez_linterna_ambient1");
	public static final SoundEvent PEZ_LINTERNA_AMBIENT2 = register("pez_linterna_ambient2");
	public static final SoundEvent PEZ_LINTERNA_HURT = register("pez_linterna_hurt");
	public static final SoundEvent PEZ_LINTERNA_DEATH = register("pez_linterna_death");
	public static final SoundEvent PEZ_LINTERNA_BITE = register("pez_linterna_bite");
	public static final SoundEvent PEZ_LINTERNA_LANTERN_CHARGE = register("pez_linterna_lantern_charge");
	public static final SoundEvent PEZ_LINTERNA_LANTERN_FLASH = register("pez_linterna_lantern_flash");

	private static SoundEvent register(String name) {
		Identifier id = Identifier.of(MoonDrip.MOD_ID, name);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

	// Se llama al arrancar el mod para que los sonidos queden registrados
	public static void register() {
	}
}

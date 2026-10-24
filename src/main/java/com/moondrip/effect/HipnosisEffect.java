package com.moondrip.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.tag.FluidTags;

// Hipnosis: la luz de la linterna del Pez Linterna.
// Dura 120 ticks (6 s). Cada 40 ticks (2 s): medio corazon de dano y saca 75 ticks de aire (2,5 burbujas).
// La inmovilizacion en si la hace KeyboardInputMixin (cliente), que anula las teclas de movimiento.
public class HipnosisEffect extends StatusEffect {

	public HipnosisEffect() {
		super(StatusEffectCategory.HARMFUL, 0x9B30FF);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return duration % 40 == 0;
	}

	@Override
	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (entity.getWorld().isClient()) {
			return true;
		}
		// Medio corazon = 1.0 de dano
		entity.damage(entity.getDamageSources().magic(), 1.0F);
		// Solo saca aire si tiene la cabeza bajo el agua.
		// El minimo es -19 porque el juego solo avisa de ahogo cuando el aire baja justo hasta -20.
		if (entity.isSubmergedIn(FluidTags.WATER)) {
			entity.setAir(Math.max(entity.getAir() - 75, -19));
		}
		return true;
	}
}

package com.moondrip.entity;

import com.moondrip.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class SolarCreeperEntity extends CreeperEntity {

	// Cambia estos valores a tu gusto
	private static final double EFFECT_RADIUS = 7.0;   // bloques alrededor del creeper
	private static final float FIRE_SECONDS = 5.0F;    // segundos en llamas
	private static final int BLIND_TICKS = 100;        // 20 ticks = 1 segundo (100 = 5 s)
	private static final float RING_VOLUME = 1.0F;     // volumen del pitido (0.0 a 1.0)
	private static final float RING_PITCH = 1.0F;      // tono del pitido (mas alto = mas agudo)

	// Para aplicar los efectos una sola vez por explosion
	private boolean effectsApplied = false;

	public SolarCreeperEntity(EntityType<? extends CreeperEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createSolarCreeperAttributes() {
		// Igual que el creeper normal, pero con mas vida
		return CreeperEntity.createCreeperAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0);
	}

	@Override
	public void tick() {
		// Justo antes de explotar (la mecha esta casi completa), aplicamos los efectos.
		// Es 1-2 ticks antes de la explosion; los jugadores que mueran por el daño pierden los efectos solos.
		if (!this.getWorld().isClient && this.isAlive()) {
			float fuse = this.getClientFuseTime(1.0F);
			if (!this.effectsApplied && fuse >= 1.0F) {
				this.effectsApplied = true;
				this.applySolarEffects();
			} else if (this.effectsApplied && fuse < 0.5F) {
				// Si la mecha se cancelo, permitimos que vuelva a pasar
				this.effectsApplied = false;
			}
		}

		super.tick();
	}

	private void applySolarEffects() {
		if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

		// Flash solar y particulas doradas
		double x = this.getX();
		double y = this.getBodyY(0.5);
		double z = this.getZ();
		serverWorld.spawnParticles(ParticleTypes.FLASH, x, y, z, 3, 0.0, 0.0, 0.0, 0.0);
		serverWorld.spawnParticles(ParticleTypes.END_ROD, x, y, z, 60, 0.7, 0.7, 0.7, 0.2);

		double radiusSq = EFFECT_RADIUS * EFFECT_RADIUS;
		for (PlayerEntity player : serverWorld.getPlayers()) {
			if (!player.isAlive() || player.isSpectator()) continue;
			if (player.squaredDistanceTo(this) > radiusSq) continue;

			player.setOnFireFor(FIRE_SECONDS);
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, BLIND_TICKS, 0));

			// Pitido solo para los jugadores que quedaron dentro del radio
			if (player instanceof ServerPlayerEntity serverPlayer) {
				serverPlayer.playSoundToPlayer(ModSounds.SOLAR_FLASH_RING, SoundCategory.HOSTILE, RING_VOLUME, RING_PITCH);
			}
		}
	}
}

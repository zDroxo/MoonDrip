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

	// ---- Cambia estos valores a tu gusto ----

	// Explosion. El creeper normal usa 3 (crater de ~5 de ancho). 5 da un crater de ~8 de ancho.
	// Cuidado: subirlo tambien aumenta el daño de la explosion.
	private static final float EXPLOSION_POWER = 5.0F;

	private static final double EFFECT_RADIUS = 7.0;   // bloques alrededor del creeper (fuego y ceguera)
	private static final float FIRE_SECONDS = 5.0F;    // segundos en llamas
	private static final int BLIND_TICKS = 100;        // 20 ticks = 1 segundo (100 = 5 s)
	private static final float RING_VOLUME = 1.0F;     // volumen del pitido (0.0 a 1.0)
	private static final float RING_PITCH = 1.0F;      // tono del pitido (mas alto = mas agudo)

	public SolarCreeperEntity(EntityType<? extends CreeperEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createSolarCreeperAttributes() {
		// Igual que el creeper normal, pero con mas vida
		return CreeperEntity.createCreeperAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0);
	}

	@Override
	public void tick() {
		// Cuando la mecha se completa, hacemos nuestra propia explosion (en vez de la del creeper normal)
		if (!this.getWorld().isClient && this.isAlive()) {
			if (this.getClientFuseTime(1.0F) >= 1.0F) {
				this.explodeSolar();
				return;
			}
		}

		super.tick();
	}

	private void explodeSolar() {
		if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

		double x = this.getX();
		double y = this.getBodyY(0.5);
		double z = this.getZ();

		// Flash solar y particulas doradas
		serverWorld.spawnParticles(ParticleTypes.FLASH, x, y, z, 3, 0.0, 0.0, 0.0, 0.0);
		serverWorld.spawnParticles(ParticleTypes.END_ROD, x, y, z, 60, 0.7, 0.7, 0.7, 0.2);

		double effectSq = EFFECT_RADIUS * EFFECT_RADIUS;

		for (PlayerEntity player : serverWorld.getPlayers()) {
			if (!player.isAlive() || player.isSpectator()) continue;

			// Fuego y ceguera solo dentro del radio de efectos
			if (player.squaredDistanceTo(this) <= effectSq) {
				player.setOnFireFor(FIRE_SECONDS);
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, BLIND_TICKS, 0));
			}
		}

		// La explosion en si (respeta la regla "mobGriefing" del mundo)
		serverWorld.createExplosion(this, this.getX(), this.getY(), this.getZ(), EXPLOSION_POWER, World.ExplosionSourceType.MOB);
		this.discard();

		// Pitido solo para los jugadores que quedaron dentro del radio de efectos
		this.playRing(serverWorld, effectSq);
	}

	// Pitido tipo "auriculares": suena despues de la explosion
	private void playRing(ServerWorld serverWorld, double radiusSq) {
		for (PlayerEntity player : serverWorld.getPlayers()) {
			if (!player.isAlive() || player.isSpectator()) continue;
			if (player.squaredDistanceTo(this) > radiusSq) continue;

			if (player instanceof ServerPlayerEntity serverPlayer) {
				serverPlayer.playSoundToPlayer(ModSounds.SOLAR_FLASH_RING, SoundCategory.HOSTILE, RING_VOLUME, RING_PITCH);
			}
		}
	}
}

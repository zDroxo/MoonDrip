package com.moondrip.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class SolarCreeperEntity extends CreeperEntity {

	// Cambia estos valores a tu gusto
	private static final double EFFECT_RADIUS = 6.0;   // bloques alrededor de la explosion
	private static final float FIRE_SECONDS = 5.0F;    // segundos en llamas
	private static final int BLIND_TICKS = 100;        // 20 ticks = 1 segundo (100 = 5 s)

	public SolarCreeperEntity(EntityType<? extends CreeperEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createSolarCreeperAttributes() {
		// Igual que el creeper normal, pero con mas vida
		return CreeperEntity.createCreeperAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0);
	}

	@Override
	public void tick() {
		super.tick();
		// Si ya exploto en este tick (se elimino con la mecha completa), hacemos el flash solar
		if (!this.getWorld().isClient && this.isRemoved() && this.getClientFuseTime(1.0F) >= 1.0F) {
			if (this.getWorld() instanceof ServerWorld serverWorld) {
				double x = this.getX();
				double y = this.getBodyY(0.5);
				double z = this.getZ();
				serverWorld.spawnParticles(ParticleTypes.FLASH, x, y, z, 3, 0.0, 0.0, 0.0, 0.0);
				serverWorld.spawnParticles(ParticleTypes.END_ROD, x, y, z, 60, 0.7, 0.7, 0.7, 0.2);

				// Efectos para los jugadores que sobrevivieron a la explosion
				double radiusSq = EFFECT_RADIUS * EFFECT_RADIUS;
				for (PlayerEntity player : serverWorld.getPlayers()) {
					if (!player.isAlive() || player.isSpectator()) continue;
					if (player.squaredDistanceTo(x, y, z) > radiusSq) continue;

					player.setOnFireFor(FIRE_SECONDS);
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, BLIND_TICKS, 0));
				}
			}
		}
	}
}

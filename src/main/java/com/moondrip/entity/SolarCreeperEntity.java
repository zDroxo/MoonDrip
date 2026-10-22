package com.moondrip.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class SolarCreeperEntity extends CreeperEntity {

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
			}
		}
	}
}

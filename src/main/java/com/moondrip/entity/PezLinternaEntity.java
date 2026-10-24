package com.moondrip.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// Pez Linterna: pez abisal. Por ahora solo existe y nada (las habilidades van al final).
public class PezLinternaEntity extends WaterCreatureEntity {

	public PezLinternaEntity(EntityType<? extends PezLinternaEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new PezLinternaMoveControl(this);
	}

	public static DefaultAttributeContainer.Builder createPezLinternaAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimAroundGoal(this, 1.0, 40));
		this.goalSelector.add(1, new LookAroundGoal(this));
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	// Movimiento bajo el agua (igual que los peces normales)
	@Override
	public void travel(Vec3d movementInput) {
		if (this.isLogicalSideForUpdatingMovement() && this.isTouchingWater()) {
			this.updateVelocity(0.01F, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
			if (this.getTarget() == null) {
				this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(movementInput);
		}
	}

	static class PezLinternaMoveControl extends MoveControl {
		private final PezLinternaEntity fish;

		PezLinternaMoveControl(PezLinternaEntity fish) {
			super(fish);
			this.fish = fish;
		}

		@Override
		public void tick() {
			if (this.fish.isSubmergedIn(FluidTags.WATER)) {
				this.fish.setVelocity(this.fish.getVelocity().add(0.0, 0.005, 0.0));
			}

			if (this.state == MoveControl.State.MOVE_TO && !this.fish.getNavigation().isIdle()) {
				float speed = (float) (this.speed * this.fish.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
				this.fish.setMovementSpeed(MathHelper.lerp(0.125F, this.fish.getMovementSpeed(), speed));
				double dx = this.targetX - this.fish.getX();
				double dy = this.targetY - this.fish.getY();
				double dz = this.targetZ - this.fish.getZ();
				if (dy != 0.0) {
					double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
					this.fish.setVelocity(this.fish.getVelocity().add(0.0, (double) this.fish.getMovementSpeed() * (dy / dist) * 0.1, 0.0));
				}
				if (dx != 0.0 || dz != 0.0) {
					float yaw = (float) (MathHelper.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
					this.fish.setYaw(this.wrapDegrees(this.fish.getYaw(), yaw, 90.0F));
					this.fish.bodyYaw = this.fish.getYaw();
				}
			} else {
				this.fish.setMovementSpeed(0.0F);
			}
		}
	}
}

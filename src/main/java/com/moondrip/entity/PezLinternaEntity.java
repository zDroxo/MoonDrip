package com.moondrip.entity;

import java.util.EnumSet;

import com.moondrip.ModSounds;
import com.moondrip.effect.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

// Pez Linterna: pez abisal. Vive en el fondo del agua, persigue a los jugadores que estan en el agua y los muerde.
// Su linterna se carga y le aplica Hipnosis (inmoviliza, dana y saca aire) al jugador.
public class PezLinternaEntity extends WaterCreatureEntity {
	// Ticks que faltan para poder volver a morder (20 ticks = 1 segundo)
	private static final int BITE_COOLDOWN_TICKS = 40;
	private int biteCooldown = 0;

	// --- Linterna (todos los tiempos en ticks: 20 ticks = 1 segundo) ---
	// A que distancia (en bloques) puede usar la linterna
	private static final double LANTERN_RANGE = 10.0;
	// Cuanto tarda en cargarse antes de brillar
	private static final int LANTERN_CHARGE_TICKS = 40;
	// Duracion de la Hipnosis: 6 segundos
	private static final int HYPNOSIS_TICKS = 120;
	// Pausa de 15 segundos DESPUES de que termina la Hipnosis
	private static final int LANTERN_PAUSE_TICKS = 300;
	// Ticks que faltan para poder volver a usarla
	private int lanternCooldown = 0;
	// Ticks que faltan para que termine de cargarse (0 = no se esta cargando)
	private int lanternCharge = 0;

	public PezLinternaEntity(EntityType<? extends PezLinternaEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new PezLinternaMoveControl(this);
	}

	public static DefaultAttributeContainer.Builder createPezLinternaAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0)
				// Mordida: 6.0 de dano = 3 corazones
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
	}

	// --- Sonidos propios (ver ModSounds) ---

	// Sonido ambiente: elige al azar una de las 2 variantes
	@Override
	protected SoundEvent getAmbientSound() {
		return this.getRandom().nextBoolean() ? ModSounds.PEZ_LINTERNA_AMBIENT1 : ModSounds.PEZ_LINTERNA_AMBIENT2;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ModSounds.PEZ_LINTERNA_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.PEZ_LINTERNA_DEATH;
	}

	@Override
	protected void initGoals() {
		// Si tiene un objetivo, lo persigue y lo muerde
		this.goalSelector.add(0, new MeleeAttackGoal(this, 1.2, true));
		// Si no, nada hacia lugares del fondo (en vez de lugares al azar, que lo hacian subir)
		this.goalSelector.add(1, new SwimDeepGoal(this, 1.0));
		this.goalSelector.add(2, new LookAroundGoal(this));
		// Elige como objetivo a jugadores (solo si estan en el agua, ver canTarget)
		this.targetSelector.add(1, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, true));
	}

	// Solo ataca a jugadores que estan en el agua (si salen a tierra, los suelta)
	@Override
	public boolean canTarget(LivingEntity target) {
		return super.canTarget(target) && target.isTouchingWater();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.biteCooldown > 0) {
			this.biteCooldown--;
		}
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			this.tickLantern(serverWorld);
		}
	}

	// Posicion de la linterna en el mundo (punta de la antena, un poco hacia adelante)
	private Vec3d getLanternPos() {
		float yaw = this.bodyYaw * 0.017453292F;
		return new Vec3d(
				this.getX() - MathHelper.sin(yaw) * 0.25,
				this.getY() + 1.15,
				this.getZ() + MathHelper.cos(yaw) * 0.25
		);
	}

	private void tickLantern(ServerWorld world) {
		if (this.lanternCooldown > 0) {
			this.lanternCooldown--;
		}
		LivingEntity target = this.getTarget();
		boolean targetOk = target != null
				&& target.isAlive()
				&& target.isTouchingWater()
				&& this.squaredDistanceTo(target) <= LANTERN_RANGE * LANTERN_RANGE
				&& this.getVisibilityCache().canSee(target);

		if (this.lanternCharge > 0) {
			// Cargando: si el objetivo se aleja o se escapa, se cancela (sin pausa)
			if (!targetOk) {
				this.lanternCharge = 0;
				return;
			}
			this.lanternCharge--;
			Vec3d pos = this.getLanternPos();
			world.spawnParticles(ParticleTypes.WITCH, pos.x, pos.y + 0.3, pos.z, 2, 0.2, 0.2, 0.2, 0.0);
			if (this.lanternCharge == 0) {
				// Brilla: aplica la Hipnosis y arranca la pausa
				target.addStatusEffect(new StatusEffectInstance(ModEffects.HIPNOSIS, HYPNOSIS_TICKS, 0, false, true, true), this);
				this.lanternCooldown = HYPNOSIS_TICKS + LANTERN_PAUSE_TICKS;
				world.spawnParticles(ParticleTypes.WITCH, pos.x, pos.y + 0.3, pos.z, 30, 0.4, 0.4, 0.4, 0.1);
				world.spawnParticles(ParticleTypes.END_ROD, pos.x, pos.y + 0.3, pos.z, 12, 0.3, 0.3, 0.3, 0.05);
				this.playSound(ModSounds.PEZ_LINTERNA_LANTERN_FLASH, 1.0F, 1.0F);
			}
		} else if (this.lanternCooldown == 0 && targetOk && !target.hasStatusEffect(ModEffects.HIPNOSIS)) {
			// Empieza a cargarse
			this.lanternCharge = LANTERN_CHARGE_TICKS;
			this.playSound(ModSounds.PEZ_LINTERNA_LANTERN_CHARGE, 1.0F, 1.0F);
		}
	}

	// Mordida con pausa entre mordiscos (si no, morderia cada segundo)
	@Override
	public boolean tryAttack(Entity target) {
		if (this.biteCooldown > 0) {
			return false;
		}
		boolean hit = super.tryAttack(target);
		if (hit) {
			this.biteCooldown = BITE_COOLDOWN_TICKS;
			this.playSound(ModSounds.PEZ_LINTERNA_BITE, 1.0F, 1.0F);
		}
		return hit;
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	// Movimiento bajo el agua
	@Override
	public void travel(Vec3d movementInput) {
		if (this.isLogicalSideForUpdatingMovement() && this.isTouchingWater()) {
			this.updateVelocity(0.01F, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
			// Siempre tiende a hundirse suavemente (pez abisal). Sin esto un pez comun queda flotando.
			if (this.getTarget() == null) {
				this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(movementInput);
		}
	}

	// Elige un punto al azar cerca y lo baja hasta el fondo del agua
	static class SwimDeepGoal extends Goal {
		private final PezLinternaEntity fish;
		private final double speed;
		private Vec3d target;

		SwimDeepGoal(PezLinternaEntity fish, double speed) {
			this.fish = fish;
			this.speed = speed;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (!this.fish.isTouchingWater() || !this.fish.getNavigation().isIdle()) {
				return false;
			}
			if (this.fish.getRandom().nextInt(20) != 0) {
				return false;
			}
			this.target = this.findDeepSpot();
			return this.target != null;
		}

		private Vec3d findDeepSpot() {
			World world = this.fish.getWorld();
			Random random = this.fish.getRandom();
			int x = this.fish.getBlockX() + random.nextInt(17) - 8;
			int z = this.fish.getBlockZ() + random.nextInt(17) - 8;
			BlockPos.Mutable pos = new BlockPos.Mutable(x, this.fish.getBlockY(), z);
			if (!world.getFluidState(pos).isIn(FluidTags.WATER)) {
				return null;
			}
			// Baja mientras siga habiendo agua debajo (maximo 32 bloques)
			for (int i = 0; i < 32 && world.getFluidState(pos.down()).isIn(FluidTags.WATER); i++) {
				pos.move(0, -1, 0);
			}
			return new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		}

		@Override
		public void start() {
			this.fish.getNavigation().startMovingTo(this.target.x, this.target.y, this.target.z, this.speed);
		}

		@Override
		public boolean shouldContinue() {
			return !this.fish.getNavigation().isIdle();
		}

		@Override
		public void stop() {
			this.fish.getNavigation().stop();
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
			// (Quite la fuerza hacia arriba que tienen los peces comunes: este pez no flota)
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

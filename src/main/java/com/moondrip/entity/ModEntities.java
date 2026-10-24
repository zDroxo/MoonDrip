package com.moondrip.entity;

import com.moondrip.MoonDrip;
import com.moondrip.effect.ModEffects;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class ModEntities {
	// Pez Linterna: "peso" de aparicion frente a otras criaturas acuaticas del mismo bioma.
	// Mas bajo = mas raro. La rareza fina y la profundidad minima estan en PezLinternaEntity.
	private static final int PEZ_LINTERNA_SPAWN_WEIGHT = 4;

	public static final EntityType<SolarCreeperEntity> SOLAR_CREEPER = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(MoonDrip.MOD_ID, "solar_creeper"),
			FabricEntityTypeBuilder.<SolarCreeperEntity>createMob()
					.spawnGroup(SpawnGroup.MONSTER)
					.entityFactory(SolarCreeperEntity::new)
					.spawnRestriction(SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark)
					.dimensions(EntityDimensions.fixed(0.6f, 1.7f))
					.trackRangeBlocks(8)
					.fireImmune()
					.build()
	);

	// Pez Linterna: criatura acuatica. Aparece solo en agua profunda (ver PezLinternaEntity.canSpawnDeep).
	public static final EntityType<PezLinternaEntity> PEZ_LINTERNA = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(MoonDrip.MOD_ID, "pez_linterna"),
			FabricEntityTypeBuilder.<PezLinternaEntity>createMob()
					.spawnGroup(SpawnGroup.WATER_CREATURE)
					.entityFactory(PezLinternaEntity::new)
					.spawnRestriction(SpawnLocationTypes.IN_WATER, Heightmap.Type.OCEAN_FLOOR, PezLinternaEntity::canSpawnDeep)
					.dimensions(EntityDimensions.fixed(0.8f, 0.8f))
					.trackRangeBlocks(8)
					.build()
	);

	public static void register() {
		ModEffects.register();

		FabricDefaultAttributeRegistry.register(SOLAR_CREEPER, SolarCreeperEntity.createSolarCreeperAttributes());
		FabricDefaultAttributeRegistry.register(PEZ_LINTERNA, PezLinternaEntity.createPezLinternaAttributes());

		// Aparece de forma natural en el Overworld, mucho menos seguido que el creeper normal
		// (el creeper normal tiene peso 100; este tiene 8). Los originales siguen existiendo.
		BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.MONSTER, SOLAR_CREEPER, 8, 1, 1);

		// Pez Linterna: en todo el Overworld (mares, lagos y cuevas), pero solo si el agua es profunda
		BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.WATER_CREATURE, PEZ_LINTERNA, PEZ_LINTERNA_SPAWN_WEIGHT, 1, 1);
	}
}

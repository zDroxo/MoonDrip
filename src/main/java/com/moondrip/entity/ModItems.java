package com.moondrip.entity;

import com.moondrip.MoonDrip;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
	public static final Item SOLAR_CREEPER_SPAWN_EGG = Registry.register(
			Registries.ITEM,
			Identifier.of(MoonDrip.MOD_ID, "solar_creeper_spawn_egg"),
			new SpawnEggItem(ModEntities.SOLAR_CREEPER, 0xF5AF23, 0x7A1010, new Item.Settings())
	);

	public static final Item PEZ_LINTERNA_SPAWN_EGG = Registry.register(
			Registries.ITEM,
			Identifier.of(MoonDrip.MOD_ID, "pez_linterna_spawn_egg"),
			new SpawnEggItem(ModEntities.PEZ_LINTERNA, 0x4A6B54, 0x5C1C7D, new Item.Settings())
	);

	public static final Item SOLAR_CREEPER_TOTEM = Registry.register(
			Registries.ITEM,
			Identifier.of(MoonDrip.MOD_ID, "solar_creeper_totem"),
			new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON))
	);

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
			entries.add(SOLAR_CREEPER_SPAWN_EGG);
			entries.add(PEZ_LINTERNA_SPAWN_EGG);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> entries.add(SOLAR_CREEPER_TOTEM));
	}
}

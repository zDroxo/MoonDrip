package com.moondrip;

import com.moondrip.entity.ModEntities;
import com.moondrip.entity.ModItems;
import net.fabricmc.api.ModInitializer;

public class MoonDrip implements ModInitializer {
	public static final String MOD_ID = "moondrip";

	@Override
	public void onInitialize() {
		ModEntities.register();
		ModItems.register();
	}
}

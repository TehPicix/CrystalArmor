package me.tehpicix.crystalarmor;

import me.tehpicix.crystalarmor.config.Config;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {

	public static final String MOD_ID = "crystal-armor";

	@Override
	public void onInitialize() {
		Config.load();
	}

}
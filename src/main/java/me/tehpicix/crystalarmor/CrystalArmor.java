package me.tehpicix.crystalarmor;

import me.tehpicix.crystalarmor.config.Config;
import net.fabricmc.api.ModInitializer;

public class CrystalArmor implements ModInitializer {

	public static final String MOD_ID = "crystal-armor";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	// public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Config.load();
	}

}
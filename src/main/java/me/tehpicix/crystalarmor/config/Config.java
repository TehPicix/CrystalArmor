package me.tehpicix.crystalarmor.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class Config {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("crystal_armor.json");

    // === Your Config Fields ===
    public Boolean enabled = true;
    public Integer radius = 11;
	public Boolean useTracing = true;
    public Integer cooldown = 20;

    // === Singleton Instance ===
    public static Config INSTANCE = new Config();

    // === Load Config ===
    public static void load() {
        try {
            File file = CONFIG_PATH.toFile();
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    INSTANCE = GSON.fromJson(reader, Config.class);
                }
            } else {
                save(); // write defaults if file doesn't exist
            }
        } catch (Exception e) {
            System.err.println("[CrystalArmor] Failed to load config:");
            e.printStackTrace();
        }
    }

    // === Save Config ===
    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(INSTANCE, writer);
        } catch (Exception e) {
            System.err.println("[CrystalArmor] Failed to save config:");
            e.printStackTrace();
        }
    }
}

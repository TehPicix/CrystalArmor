package me.tehpicix.crystalarmor.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    public static Screen create(Screen parent) {
    ConfigBuilder builder = ConfigBuilder.create()
        .setParentScreen(parent)
        .setTitle(Text.literal("Crystal Armor Config"));

    ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));

    // Must add at least one entry or category won't be valid
    builder.entryBuilder().startTextDescription(Text.literal("No options available yet"))
        .build();

    // If you want to save something later
    builder.setSavingRunnable(() -> {
        // save your config here
    });

    return builder.build();
}

}
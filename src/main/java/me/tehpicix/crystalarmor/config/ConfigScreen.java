package me.tehpicix.crystalarmor.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    public static Screen create(Screen parent) {

        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.literal("Crystal Armor Config"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Create the general category
        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));

        general.addEntry(entryBuilder
            .startBooleanToggle(Text.literal("Enable Swapping"), Config.INSTANCE.enabled)
            .setDefaultValue(true)
            .setTooltip(Text.literal("Swaps to your most blast resistant armor when around end crystals"))
            .setSaveConsumer(newValue -> {
                Config.INSTANCE.enabled = newValue;
                Config.save();
            })
            .build());
        
        general.addEntry(entryBuilder
            .startIntSlider(Text.literal("Crystal Radius"), Config.INSTANCE.radius, 0, 25)
            .setDefaultValue(11)
            .setTooltip(Text.literal("How close before the armor swap occurs"))
            .setSaveConsumer(newValue -> {
                Config.INSTANCE.radius = newValue;
                Config.save();
            })
            .build());
            
        general.addEntry(entryBuilder
            .startBooleanToggle(Text.literal("Require Path Tracing"), true)
            .setDefaultValue(true)
            .setTooltip(Text.literal("Requires line of sight to the crystal for swapping"))
            .setSaveConsumer(newValue -> {
                Config.INSTANCE.useTracing = newValue;
                Config.save();
            })
            .build());
            
        general.addEntry(entryBuilder
            .startIntField(Text.literal("Cooldown Time"), Config.INSTANCE.cooldown)
            .setDefaultValue(20)
            .setTooltip(Text
            .literal("Leave your blast resistant armor on for this many ticks once its safe to swap back"))
            .setSaveConsumer(newValue -> {
                Config.INSTANCE.cooldown = newValue;
                Config.save();
            })
            .build());
            
        return builder.build();
    }
}
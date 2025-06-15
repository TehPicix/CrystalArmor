package me.tehpicix.crystalarmor;

import java.util.Map;
import java.util.Set;

import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry.Reference;

public class ItemEvaluator {

    /**
     * A map of enchantments and their associated weights for calculating blast resistance.
     * The weights are used to determine the effectiveness of each enchantment in reducing blast damage.
     * Each weight is multiplied by the level of the enchantment on the item and summed up to get the total blast resistance score.
     */
    private static final Map<RegistryKey<Enchantment>, Float> ENCHANTMENT_WEIGHTS = Map.of(
        Enchantments.BLAST_PROTECTION,          2.0f,
        Enchantments.PROTECTION,                1.0f,
        Enchantments.PROJECTILE_PROTECTION,     0.5f,
        Enchantments.FIRE_PROTECTION,           0.5f
    );

    /**
     * A map of item types and their associated weights for calculating blast resistance.
     * The weights are used to determine the effectiveness of each item type in reducing blast damage.
     * The weight is multiplied by the blast resistance score calculated from enchantments.
     */
    private static final Map<Set<Item>, Float> MATERIAL_WEIGHTS = Map.of(

        Set.of(
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS
        ), 6.0f,

        Set.of(
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS
        ), 6.0f,

        Set.of(
            Items.IRON_HELMET,
            Items.IRON_CHESTPLATE,
            Items.IRON_LEGGINGS,
            Items.IRON_BOOTS
        ), 5.0f,

        Set.of(
            Items.GOLDEN_HELMET,
            Items.GOLDEN_CHESTPLATE,
            Items.GOLDEN_LEGGINGS,
            Items.GOLDEN_BOOTS
        ), 3.0f,

        Set.of(
            Items.CHAINMAIL_HELMET,
            Items.CHAINMAIL_CHESTPLATE,
            Items.CHAINMAIL_LEGGINGS,
            Items.CHAINMAIL_BOOTS
        ), 4.0f,

        Set.of(
            Items.LEATHER_HELMET,
            Items.LEATHER_CHESTPLATE,
            Items.LEATHER_LEGGINGS,
            Items.LEATHER_BOOTS
        ), 2.0f

    );              
    
    
    /**
     * Calculates the blast resistance score of an item stack based on its enchantments.
     * 
     * @param stack The item stack to evaluate.
     * @return The calculated blast resistance score.
     */
    public static float getBlastResistance(ItemStack stack) {
        float score = 0f;
        MinecraftClient client = MinecraftClient.getInstance();
        for (var entry : ENCHANTMENT_WEIGHTS.entrySet()) {
            RegistryKey<Enchantment> enchantment = entry.getKey();
            Reference<Enchantment> reg = client.world.getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getOrThrow(RegistryKey.of(RegistryKeys.ENCHANTMENT, enchantment.getValue()));
            Float weight = entry.getValue();
            int level = EnchantmentHelper.getLevel(reg, stack);
            score += level * weight;
        }
        for (var entry : MATERIAL_WEIGHTS.entrySet()) {
            var weight = entry.getValue();
            if(entry.getKey().contains(stack.getItem())) score *= weight;
        }
        return score;
    }

}

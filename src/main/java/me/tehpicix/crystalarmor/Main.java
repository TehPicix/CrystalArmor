package me.tehpicix.crystalarmor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.tehpicix.crystalarmor.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.EquipmentType;

public class Main implements ModInitializer, ClientModInitializer {

	public static final String MOD_ID = "crystal-armor";

		// Types of armor to search
    private static final Map<EquipmentType, Set<Item>> ARMOR_TYPES = Map.of(
		EquipmentType.HELMET, Set.of(
			Items.CHAINMAIL_HELMET,
			Items.IRON_HELMET,
			Items.DIAMOND_HELMET,
			Items.NETHERITE_HELMET,
			Items.LEATHER_HELMET,
			Items.GOLDEN_HELMET),
		EquipmentType.LEGGINGS, Set.of(
			Items.CHAINMAIL_LEGGINGS,
			Items.IRON_LEGGINGS,
			Items.DIAMOND_LEGGINGS,
			Items.NETHERITE_LEGGINGS,
			Items.LEATHER_LEGGINGS,
			Items.GOLDEN_LEGGINGS),
		EquipmentType.BOOTS, Set.of(
			Items.CHAINMAIL_BOOTS,
			Items.IRON_BOOTS,
			Items.DIAMOND_BOOTS,
			Items.NETHERITE_BOOTS,
			Items.LEATHER_BOOTS,
			Items.GOLDEN_BOOTS),
		EquipmentType.CHESTPLATE, Set.of(
			Items.CHAINMAIL_CHESTPLATE,
			Items.IRON_CHESTPLATE,
			Items.DIAMOND_CHESTPLATE,
			Items.NETHERITE_CHESTPLATE,
			Items.LEATHER_CHESTPLATE,
			Items.GOLDEN_CHESTPLATE)
	);

	// Store the preferred armor slots
	protected HashMap<Integer, ItemStack> loadout = new HashMap<>();
	
	// A tick counter since the last armor switch, -1 means not counting
	private int tickCounter = -1;


	@Override
	public void onInitialize() {
		Config.load();
	}

	@Override
	public void onInitializeClient() {
	  	ClientTickEvents.END_CLIENT_TICK.register(client -> {

			// Ensure we have a player instance
			if (client.player == null || client.player.getWorld() == null)
				return;

			// Increment the tick counter
			if (tickCounter >= 0)
				tickCounter++;

			// Respect the configuration setting
			if (!Config.INSTANCE.enabled)
				return;

			// If the player is in range
			boolean inRange = CrystalLocater.listCrystalsInRange(client).size() > 0;
			boolean inTrace = Config.INSTANCE.useTracing ? CrystalLocater.checkLineOfSight(client) : true;
			if (inRange && inTrace) {
				switchToBestArmor();
				return;
			}

			// If were not in range, start ticking the counter
			if (!loadout.isEmpty() && tickCounter < 0)
				tickCounter = 0;

			// If the tick counter has reached the cooldown, stop counting
			if (tickCounter >= Config.INSTANCE.cooldown) {
				tickCounter = -1;

				for (Map.Entry<Integer, ItemStack> entry : loadout.entrySet()) {
					int slot = entry.getKey();
					int currentSlot = client.player.getInventory().getSlotWithStack(entry.getValue());
					ItemManager.swapSlots(slot, currentSlot);
				}

				loadout.clear();

			}

		});
	}

	
	/**
	 * Switches the player's armor to the best available items based on the defined armor types.
	 * It iterates through each armor type, finds the best item in the player's inventory,
	 * and swaps it with the currently equipped item if it's better. Additionally, saves
	 * the current item in the loadout to swap back later.
	 */
	private void switchToBestArmor() {

		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.interactionManager == null)
			return;

		// Iterate over each armor type
		for (EquipmentType type : ARMOR_TYPES.keySet()) {
			
			// Get the slot index for the current armor type
			int slot = type.getEquipmentSlot().getEntitySlotId() + 36;
			ItemStack currentItem = client.player.getInventory().getStack(slot);
   
			// Get all items compatible with the armor type
			Set<ItemStack> equipment = new HashSet<>();
			
			// Find all items in the player's inventory of the current type
			for (int i = 0; i < client.player.getInventory().size(); i++) {
				
				// Get the item in the current slot
                ItemStack stack = client.player.getInventory().getStack(i);

                // Skip if the item is empty or not of the preferred type
                if (stack.isEmpty() || !ARMOR_TYPES.get(type).contains(stack.getItem()))
					continue;
					
				equipment.add(stack);

            }

			// If the slot isnt empty and isnt of the preferred type, skip (we don't want to replace elytra or other items. Only swap if armor)
   			if (!currentItem.isEmpty() && !ARMOR_TYPES.get(type).contains(currentItem.getItem()))
				continue;
				
			// Find the best item based on ranking
			ItemStack bestItem = null;
			for (ItemStack stack : equipment) {
				if (bestItem == null || ItemManager.rankItem(stack) > ItemManager.rankItem(bestItem))
					bestItem = stack;
			}

			// If the best item is already equipped, skip
			if (bestItem != null && bestItem.equals(currentItem) || bestItem == null)
				continue;

			// Save the current item in the loadout
			loadout.put(slot, currentItem.copy());

			// Swap the items in the inventory
			int bestItemSlot = client.player.getInventory().getSlotWithStack(bestItem);
			ItemManager.swapSlots(slot, bestItemSlot);

   		}

	}

}
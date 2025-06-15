package me.tehpicix.crystalarmor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.tehpicix.crystalarmor.ItemEvaluator;
import me.tehpicix.crystalarmor.config.Config;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

@Mixin(MinecraftServer.class)
public class WorldMixin {

	// Store the Minecraft client instance
	protected MinecraftClient client;
	
	// Ticks since last swap
	protected int ticksSinceLastSwap = -1;

	@Inject(at = @At("HEAD"), method = "loadWorld")
	private void init(CallbackInfo info) {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			// Only  run if enabled
			if(!Config.INSTANCE.enabled) return;

			// Get the player instance
			ClientPlayerEntity player = client.player;
			if (player == null || player.getWorld() == null) return;
			this.client = client;

			// Check if the player is in creative mode
			if (player.isCreative()) return;

            // Increment the tick counter
			if (ticksSinceLastSwap >= 0) ticksSinceLastSwap++;
			
			// Get the item in hand
			ItemStack itemInHand = player.getMainHandStack();
			float resistance = ItemEvaluator.getBlastResistance(itemInHand);
			player.sendMessage(Text.literal("Blast Resistance: " + resistance), true);
			
            
            // // Check if the player is has line of sight to an end crystal
            // if (checkLineOfSight()){
            //     ticksSinceLastSwap = 0;
            //     swapToBlastArmor(player);
            // }

            // // If the player has blast armor, swap it out after 20 ticks
            // else if (ticksSinceLastSwap >= 20) {
            //     swapFromBlastArmor(player);
            //     ticksSinceLastSwap = -1;
            // }
			
			
		});
	}
}
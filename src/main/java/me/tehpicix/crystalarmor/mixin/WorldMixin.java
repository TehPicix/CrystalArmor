package me.tehpicix.crystalarmor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.tehpicix.crystalarmor.CrystalLocater;
import me.tehpicix.crystalarmor.config.Config;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class WorldMixin {

	// Store the Minecraft client instance
	protected MinecraftClient client;

	@Inject(at = @At("HEAD"), method = "loadWorld")
	private void init(CallbackInfo info) {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			// Only  run if enabled
			if(!Config.INSTANCE.enabled) return;

			// Get the player instance
			ClientPlayerEntity player = client.player;
			if (player == null || player.getWorld() == null) return;
			this.client = client;

			// Determine if the player is in range of an end crystal
			if (CrystalLocater.listCrystalsInRange(this.client).size() == 0)
				return;

			// Use line of sight tracing if enabled
			if (Config.INSTANCE.useTracing && !CrystalLocater.checkLineOfSight(this.client))
				return;

			// TODO: Perform the armor swap logic			
			
		});
	}
}
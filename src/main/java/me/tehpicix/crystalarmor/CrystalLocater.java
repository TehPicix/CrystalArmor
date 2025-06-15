package me.tehpicix.crystalarmor;

import java.util.ArrayList;
import java.util.List;

import me.tehpicix.crystalarmor.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class CrystalLocater {

    /** 
     * Return the 8 corners + center of the given box.
     *  @param box The box to sample points from.
     */
    private static List<Vec3d> samplePoints(Box box) {
        List<Vec3d> pts = new ArrayList<>(9);
        pts.add(box.getCenter());
        pts.add(new Vec3d(box.minX, box.minY, box.minZ));
        pts.add(new Vec3d(box.maxX, box.minY, box.minZ));
        pts.add(new Vec3d(box.minX, box.minY, box.maxZ));
        pts.add(new Vec3d(box.maxX, box.minY, box.maxZ));
        pts.add(new Vec3d(box.minX, box.maxY, box.minZ));
        pts.add(new Vec3d(box.maxX, box.maxY, box.minZ));
        pts.add(new Vec3d(box.minX, box.maxY, box.maxZ));
        pts.add(new Vec3d(box.maxX, box.maxY, box.maxZ));
        return pts;
    }

    /**
     * Check if the the player is within the radius of any end crystal.
     * @param player The player to check.
     */
    public static List<EndCrystalEntity> listCrystalsInRange(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        Box playerBox  = player.getBoundingBox();
        return player.getWorld().getEntitiesByType(
            EntityType.END_CRYSTAL,
            playerBox.expand(Config.INSTANCE.radius),
            e -> e.squaredDistanceTo(player) <= (Config.INSTANCE.radius * Config.INSTANCE.radius));
    }
 

    /**
     * Checks if the player has a line of sight to an end crystal.
     * @param player The player to check.
     * @return true if the player has a line of sight to an end crystal, false otherwise.
     */
    public static boolean checkLineOfSight(MinecraftClient client) {

        var RADIUS = Config.INSTANCE.radius;
        if (client.player == null || client.world == null) return false;

        double radius = RADIUS;
        Box playerBox  = client.player.getBoundingBox();
        List<? extends Entity> crystals = client.world.getEntitiesByType(
            EntityType.END_CRYSTAL,
            playerBox.expand(radius),
            e -> e.squaredDistanceTo(client.player) <= radius*radius
        );

        for (Entity crystal : crystals) {
            Box crystalBox = crystal.getBoundingBox();

            for (Vec3d from : samplePoints(playerBox)) {
                for (Vec3d to : samplePoints(crystalBox)) {
                    if (from.squaredDistanceTo(to) > radius*radius) continue;

                    BlockHitResult hit = client.world.raycast(
                        new RaycastContext(
                            from, to,
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            client.player
                        )
                    );

                    if (hit.getType() == HitResult.Type.MISS) {
                        // no block in between
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
    
}

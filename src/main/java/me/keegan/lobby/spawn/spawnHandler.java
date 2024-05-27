package me.keegan.lobby.spawn;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.locationUtil.coordsIsInCoordinates;
import static me.keegan.utils.locationUtil.yInIsYPoints;

public class spawnHandler implements Listener {
    // list of top point to opposite bottom point x, z and y
    private static final List<Integer[]> spawnCoordinates = new ArrayList<Integer[]>(){{
        add(new Integer[]{17, 292, 256}); // TOP LEFT (X, Z, Y)
        add(new Integer[]{-27, 341, 79}); // BOTTOM RIGHT (X, Z, Y)
    }};

    // 17, 341
    // -27, 292

    public static boolean isInSpawn(LivingEntity livingEntity) {
        if (livingEntity == null
                || livingEntity.getWorld().getEnvironment() != World.Environment.NORMAL) { return false; }

        Integer[] entityCoords = new Integer[]{
                (int) livingEntity.getLocation().getX(),
                (int) livingEntity.getLocation().getZ(),
                (int) livingEntity.getLocation().getY()};

        return coordsIsInCoordinates(entityCoords, spawnCoordinates.get(0), spawnCoordinates.get(1))
                && yInIsYPoints(entityCoords[2], spawnCoordinates.get(0)[2], spawnCoordinates.get(1)[2]);
    }
}

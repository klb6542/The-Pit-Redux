package me.keegan.lobby.enchantedlake;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.locationUtil.coordsIsInCoordinates;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class enchantedLakeHandler implements Listener {
    // list of top point to opposite bottom point x and z only
    private static final List<Integer[]> enchantedLakeCoordinates = new ArrayList<Integer[]>(){{
        add(new Integer[]{-98, 180}); // TOP LEFT
        add(new Integer[]{0, 275}); // BOTTOM RIGHT
    }};

    @EventHandler(priority = EventPriority.HIGH)
    public void playerFished(PlayerFishEvent e) {
        Location location = e.getPlayer().getLocation();
        Integer[] playerCoords = new Integer[]{(int) location.getX(), (int) location.getZ()};

        if (!coordsIsInCoordinates(playerCoords, enchantedLakeCoordinates.get(0), enchantedLakeCoordinates.get(1))
                || e.getState() != PlayerFishEvent.State.CAUGHT_FISH
                || e.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) { return; }

        if (e.getCaught() != null) {
            Item item = (Item) e.getCaught();
            ItemStack itemStack = item.getItemStack();

            itemStack.setAmount(itemStack.getAmount() * 2);
        }

        e.setExpToDrop(e.getExpToDrop() * 2);
    }
}

package me.keegan.handlers;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class playerDamageHandler implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST) // executed last
    public void playerDamaged(EntityDamageByEntityEvent e) {
       // Entity player = e.getEntity();
       // if (!(player instanceof Player)) { return; }

       // ThePitRedux.getPlugin().getLogger().info("Priority High");
       // e.setDamage(0);
    }
}

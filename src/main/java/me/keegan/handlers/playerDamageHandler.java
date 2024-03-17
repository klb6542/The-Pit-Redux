package me.keegan.handlers;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class playerDamageHandler implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void playerDamaged(EntityDamageEvent e) {
        Entity player = e.getEntity();
        if (!(player instanceof Player)) { return; }

        ThePitRedux.getPlugin().getLogger().info(String.valueOf(e.getDamage()));
        ThePitRedux.getPlugin().getLogger().info(String.valueOf(e.getFinalDamage()));
        e.setDamage(0);
    }
}

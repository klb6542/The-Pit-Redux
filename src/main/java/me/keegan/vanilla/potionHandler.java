package me.keegan.vanilla;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class potionHandler implements Listener {
    private final List<EntityPotionEffectEvent.Cause> potionCauses = new ArrayList<>(Arrays.asList(
            EntityPotionEffectEvent.Cause.POTION_DRINK,
            EntityPotionEffectEvent.Cause.POTION_SPLASH,
            EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD
    ));

    @EventHandler
    public void entityPotionEffect(EntityPotionEffectEvent e) {
        e.setCancelled(potionCauses.contains(e.getCause()));
    }
}

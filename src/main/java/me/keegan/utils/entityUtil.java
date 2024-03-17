package me.keegan.utils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class entityUtil {
    public static @Nullable LivingEntity getDamager(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
                e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION &&
                e.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) { return null; }

        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;

        return (event.getDamager() instanceof LivingEntity) ? ((LivingEntity) event.getEntity()) : null;
    }
}

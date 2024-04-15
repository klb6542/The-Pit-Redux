package me.keegan.utils;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.Nullable;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class entityUtil {
    public static @Nullable LivingEntity getDamager(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
                e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION &&
                e.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) { return null; }

        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;

        return (event.getDamager() instanceof LivingEntity) ? ((LivingEntity) event.getEntity()) : null;
    }

    public static Boolean damagerIsArrow(EntityDamageByEntityEvent e) {
        return e.getEntity() instanceof LivingEntity
                && e.getDamager() instanceof Arrow
                && e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE
                && ((Arrow) e.getDamager()).getShooter() instanceof LivingEntity;
    }

    public static Boolean damagerIsSnowball(EntityDamageByEntityEvent e) {
        return e.getEntity() instanceof LivingEntity
                && e.getDamager() instanceof Snowball
                && e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE
                && ((Snowball) e.getDamager()).getShooter() instanceof LivingEntity;
    }

    public static Boolean projectileIsArrow(ProjectileHitEvent e) {
        return e.getEntity() instanceof Arrow
                && e.getEntity().getShooter() instanceof LivingEntity;
    }
}

package me.keegan.utils;

/*
 * Copyright (c) 2024. Created by klb.
 */

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class enchantUtil implements Listener {
    public abstract Material[] getEnchantMaterial();
    public abstract String getName();
    public abstract String getEnchantName();
    public abstract String[] getEnchantDescription();
    public abstract Number getMaxLevel();
    public abstract boolean isRareEnchant();
    public abstract void executeEnchant(LivingEntity damaged, LivingEntity damager, EntityDamageByEntityEvent e, Object... args);

    public void attemptEnchantExecution(LivingEntity damaged, LivingEntity damager, EntityDamageByEntityEvent e, Object... args) {
        this.executeEnchant(damaged, damager, e, args);
    }
}

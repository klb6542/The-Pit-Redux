package me.keegan.utils;

/*
 * Copyright (c) 2024. Created by klb.
 */

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

public abstract class enchantUtil {
    public abstract Material[] getEnchantMaterial();
    public abstract String getName();
    public abstract String getEnchantName();
    public abstract String[] getEnchantDescription();
    public abstract Number getMaxLevel();
    public abstract boolean isRareEnchant();
    public abstract void procEnchant(LivingEntity damaged, LivingEntity damager, Object... args);
}

package me.keegan.utils;

/*
 * Copyright (c) 2024. Created by klb.
 */

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class enchantUtil implements Listener {
    public abstract Material[] getEnchantMaterial();
    public abstract String getName();
    public abstract String[] getEnchantDescription();
    public abstract Integer getMaxLevel();
    public abstract boolean isRareEnchant();
    public abstract void executeEnchant(Object... args);

    // below is included incase there is an enchantment to reduce/cancel healing or any other methods
    public void heal(LivingEntity entity, Double amount) {
        entity.setHealth(entity.getHealth() + amount);
    }

    /*
     * Object... args contains:

     * args[0] = event
     * args[1] = itemstack to check for enchant
     * args[2] = enchant that is being attempted to execute
     -------------------------------------------------------------------------------------------------------------------
     * Object... args returns:

     * args[0] = event
     * args[1] = itemstack
     * args[2] = enchant level - 1
     */

    public void attemptEnchantExecution(Object[] args) {
        if (args[1] == null) { return; }
        if (!mysticUtil.getInstance().hasEnchant((ItemStack) args[1], (enchantUtil) args[2])) { return; }

        args[2] = mysticUtil.getInstance().getEnchantLevel((ItemStack) args[1], (enchantUtil) args[2]) - 1;
        this.executeEnchant(args);
    }
}

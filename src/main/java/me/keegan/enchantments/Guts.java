package me.keegan.enchantments;

import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Guts extends enchantUtil implements Listener {
    private final Double[] healingPerLevel = new Double[]{0.25, 0.5, 1.0};

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.GOLDEN_SWORD};
    }

    @Override
    public String getName() {
        return "Guts";
    }

    @Override
    public String getEnchantName() {
        return this.getName();
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Heal {1}{2}❤{0} on kill", gray, red, healingPerLevel[0]),
                MessageFormat.format("{0}Heal {1}{2}❤{0} on kill", gray, red, healingPerLevel[1]),
                MessageFormat.format("{0}Heal {1}{2}❤{0} on kill", gray, red, healingPerLevel[2])
        };
    }

    @Override
    public Integer getMaxLevel() {
        return this.healingPerLevel.length;
    }

    @Override
    public boolean isRareEnchant() {
        return false;
    }

    @Override
    public void procEnchant(LivingEntity damaged, LivingEntity damager, Object... args) {

    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();

        if (!(damaged instanceof LivingEntity) || !(damager instanceof  LivingEntity)) { return; }


    }
}

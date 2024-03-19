package me.keegan.enchantments;

import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Guts extends enchantUtil {
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
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Heal {1}{2}❤{0} on kill", gray, red, healingPerLevel[0]),
                MessageFormat.format("{0}Heal {1}{2}❤{0} on kill", gray, red, healingPerLevel[1]),
                MessageFormat.format("{0}Heal {1}{2}❤{0} on kill", gray, red, healingPerLevel[2])
        };
    }

    @Override
    public Integer getMaxLevel() {
        return this.getEnchantDescription().length;
    }

    @Override
    public boolean isRareEnchant() {
        return false;
    }

    @Override
    public void executeEnchant(LivingEntity damaged, LivingEntity damager, EntityDamageByEntityEvent e, Object... args) {
        ThePitRedux.getPlugin().getLogger().info(damager.getName() + " attacked a living entity!");
    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();

        if (!(damaged instanceof LivingEntity) || !(damager instanceof  LivingEntity)) { return; }
        Object[] args = new Object[]{};

        this.attemptEnchantExecution((LivingEntity) damaged, (LivingEntity) damager, e, args);
    }
}

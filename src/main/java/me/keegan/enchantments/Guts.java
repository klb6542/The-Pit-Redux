package me.keegan.enchantments;

import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Guts extends enchantUtil {
    private final Double[] healingPerLevel = new Double[]{0.5, 1.0, 2.0};

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
                MessageFormat.format("{0}Heal {1}{2}❤{0} on kill", gray, red, Math.floor(healingPerLevel[0] / 2)),
                MessageFormat.format("{0}Heal {1}{2}❤{0} on kill", gray, red, Math.floor(healingPerLevel[1] / 2)),
                MessageFormat.format("{0}Heal {1}{2}❤{0} on kill", gray, red, Math.floor(healingPerLevel[2] / 2))
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
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];

        EntityDeathEvent e = (EntityDeathEvent) args[0];
        LivingEntity killer = e.getEntity().getKiller();

        this.heal(killer, healingPerLevel[enchantLevel]);
    }

    @EventHandler
    public void entityKilled(EntityDeathEvent e) {
        LivingEntity damaged = e.getEntity();
        if (damaged.getKiller() == null) { return; }

        Object[] args = new Object[]{
                e,
                damaged.getKiller().getInventory().getItemInMainHand(),
                this
        };

        this.attemptEnchantExecution(args);
    }
}

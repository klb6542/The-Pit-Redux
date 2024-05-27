package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class SpeedyKill extends enchantUtil {
    private final Integer[] speedAmplifierPerLevel = new Integer[]{0, 0, 1};
    private final Integer[] speedDurationPerLevel = new Integer[]{6, 9, 6};

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.GOLDEN_SWORD};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.NORMAL;
    }

    @Override
    public String getName() {
        return "Speedy Kill";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Gain {1}Speed {2} {0}({3}s) on kill", gray, yellow,
                        integerToRoman(speedAmplifierPerLevel[0] + 1, false), speedDurationPerLevel[0]),

                MessageFormat.format("{0}Gain {1}Speed {2} {0}({3}s) on kill", gray, yellow,
                        integerToRoman(speedAmplifierPerLevel[1] + 1, false), speedDurationPerLevel[1]),

                MessageFormat.format("{0}Gain {1}Speed {2} {0}({3}s) on kill", gray, yellow,
                        integerToRoman(speedAmplifierPerLevel[2] + 1, false), speedDurationPerLevel[2]),
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
    public boolean isMysticWellEnchant() {
        return true;
    }

    @Override
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];

        EntityDeathEvent e = (EntityDeathEvent) args[0];
        LivingEntity killer = e.getEntity().getKiller();

        this.setSpeed(killer, speedAmplifierPerLevel[enchantLevel], speedDurationPerLevel[enchantLevel].doubleValue());
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

        this.attemptEnchantExecution(damaged, args);
    }
}

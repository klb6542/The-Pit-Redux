package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */


public class Singularity extends enchantUtil {
    private final Integer[] maxDamagePerLevel = new Integer[]{5, 4, 2};

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.LEATHER_LEGGINGS};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.NORMAL;
    }

    @Override
    public String getName() {
        return "Singularity";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Hits you receive deal at most {1}{2}❤/n" +
                        "{0}damage", gray, red, (double) (maxDamagePerLevel[0] / 2)),

                MessageFormat.format("{0}Hits you receive deal at most {1}{2}❤/n" +
                        "{0}damage", gray, red, (double) (maxDamagePerLevel[1] / 2)),

                MessageFormat.format("{0}Hits you receive deal at most {1}{2}❤/n" +
                        "{0}damage", gray, red, (double) (maxDamagePerLevel[2] / 2)),
        };
    }

    @Override
    public Integer getMaxLevel() {
        return this.getEnchantDescription().length;
    }

    @Override
    public boolean isRareEnchant() {
        return true;
    }

    @Override
    public boolean isMysticWellEnchant() {
        return true;
    }

    @Override
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];

        playerDamageHandler.getInstance().setMaxDamage(e, maxDamagePerLevel[enchantLevel]);
    }

    // executes after all damage has been put in the playerDamageHandler
    @EventHandler(priority = EventPriority.HIGH)
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if ((!(e.getEntity() instanceof LivingEntity)
                || !(e.getDamager() instanceof LivingEntity))
                && !entityUtil.damagerIsArrow(e)) { return; }

        LivingEntity damaged = (LivingEntity) e.getEntity();

        Object[] args = new Object[]{
                e,
                (damaged.getEquipment() != null) ? damaged.getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(args);
    }
}

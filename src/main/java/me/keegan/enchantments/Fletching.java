package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Fletching extends enchantUtil {
    private final Integer[] damagePerLevel = new Integer[]{10, 15, 23};

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.BOW};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.NORMAL;
    }

    @Override
    public String getName() {
        return "Fletching";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Deal {1}+{2}%{0} bow damage", gray, red, damagePerLevel[0]),
                MessageFormat.format("{0}Deal {1}+{2}%{0} bow damage", gray, red, damagePerLevel[1]),
                MessageFormat.format("{0}Deal {1}+{2}%{0} bow damage", gray, red, damagePerLevel[2]),
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
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];

        playerDamageHandler.getInstance().addDamage(e, damagePerLevel[enchantLevel]);
    }

    @EventHandler
    public void arrowDamaged(EntityDamageByEntityEvent e) {
        if (!entityUtil.damagerIsArrow(e)) { return; }

        Arrow arrow = (Arrow) e.getDamager();
        LivingEntity shooter = (LivingEntity) arrow.getShooter();

        Object[] args = new Object[]{
                e,
                (shooter.getEquipment() != null && shooter.getEquipment().getItemInMainHand().getType() == Material.BOW)
                        ? shooter.getEquipment().getItemInMainHand()
                        : shooter.getEquipment().getItemInOffHand(),
                this
        };

        this.attemptEnchantExecution(args);
    }
}

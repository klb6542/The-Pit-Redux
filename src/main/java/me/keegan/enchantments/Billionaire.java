package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Billionaire extends enchantUtil {
    private final Integer[] damagePerLevel = new Integer[]{33, 66, 100};
    private final Integer[] xpLevelsCostPerLevel = new Integer[]{1, 2, 3};

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
        return "Billionaire";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Hits with this sword deal {1}+{3}%/n"
                        + "{1}damage{0} but costs{2} {4} XP levels",
                        gray, red, aqua, damagePerLevel[0], xpLevelsCostPerLevel[0]),

                MessageFormat.format("{0}Hits with this sword deal {1}+{3}%/n"
                                + "{1}damage{0} but costs{2} {4} XP levels",
                        gray, red, aqua, damagePerLevel[1], xpLevelsCostPerLevel[1]),

                MessageFormat.format("{0}Hits with this sword deal {1}+{3}%/n"
                                + "{1}damage{0} but costs{2} {4} XP levels",
                        gray, red, aqua, damagePerLevel[2], xpLevelsCostPerLevel[2])
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
        LivingEntity damager = (LivingEntity) e.getDamager();

        // making sure it works with mobs
        if (damager instanceof Player && ((Player) damager).getLevel() < xpLevelsCostPerLevel[enchantLevel]) { return; }

        if (damager instanceof Player) {
            ((Player) damager).setLevel(((Player) damager).getLevel() - xpLevelsCostPerLevel[enchantLevel]);
            ((Player) damager).playSound(damager.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5f, 0.74f);
        }

        playerDamageHandler.getInstance().addDamage(e, (double) damagePerLevel[enchantLevel]);
    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity) || !(e.getDamager() instanceof LivingEntity)) { return; }

        LivingEntity damager = (LivingEntity) e.getDamager();

        Object[] args = new Object[]{
                e,
                (damager.getEquipment() != null) ? damager.getEquipment().getItemInMainHand() : null,
                this
        };

        this.attemptEnchantExecution(damager, args);
    }
}

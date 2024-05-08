package me.keegan.enchantments;

/*
 * Copyright (c) 2024. Created by klb.
 */

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

public class Royalty extends enchantUtil {
    // USED ONLY FOR HELMETS

    private final int xpMultiplier = 2;

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.GOLDEN_HELMET};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.HELMET;
    }

    @Override
    public String getName() {
        return "Royalty";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Earn {1}+{2}% XP{0} from kills", gray, aqua, xpMultiplier * 100)
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
        return false;
    }

    @Override
    public void executeEnchant(Object[] args) {
        EntityDeathEvent e = (EntityDeathEvent) args[0];
        e.setDroppedExp(e.getDroppedExp() * xpMultiplier);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDied(EntityDeathEvent e) {
        LivingEntity killer = e.getEntity().getKiller();
        if (killer == null || killer.getEquipment() == null) { return; }

        Object[] args = new Object[]{
                e,
                (killer.getEquipment().getHelmet() != null) ? killer.getEquipment().getHelmet() : null,
                this
        };

        this.attemptEnchantExecution(args);
    }
}

package me.keegan.enchantments;

import me.keegan.enums.cooldownEnums;
import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.wordUtil.integerToOrdinal;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class ComboStun extends enchantUtil {
    private final Double[] durationPerLevel = new Double[]{0.5, 1.0, 1.5};
    private final Integer[] hitsNeededPerLevel = new Integer[]{5, 4, 4};
    private final Integer durationUntilHitsReset = 15;

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.GOLDEN_SWORD};
    }

    @Override
    public mysticEnums getEnchantType() {
        return null;
    }

    @Override
    public String getName() {
        return "Combo: Stun";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}The {1}{2}{0} strike on an enemy/n"
                        + "{0}stuns them for {3} seconds",
                        gray, yellow, integerToOrdinal(hitsNeededPerLevel[0]), durationPerLevel[0]),

                MessageFormat.format("{0}The {1}{2}{0} strike on an enemy/n"
                                + "{0}stuns them for {3} second",
                        gray, yellow, integerToOrdinal(hitsNeededPerLevel[1]), durationPerLevel[1]),

                MessageFormat.format("{0}The {1}{2}{0} strike on an enemy/n"
                                + "{0}stuns them for {3} seconds",
                        gray, yellow, integerToOrdinal(hitsNeededPerLevel[2]), durationPerLevel[2]),
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
        LivingEntity damaged = (LivingEntity) e.getEntity();

        UUID damagerUniqueId = damager.getUniqueId();

        this.addHitToHitCounter(damagerUniqueId, 1);
        this.addCooldown(damagerUniqueId, (long) durationUntilHitsReset, cooldownEnums.RESET_HIT_COUNTER);

        if (this.getHitCounter(damagerUniqueId) < hitsNeededPerLevel[enchantLevel]) { return; }
        this.resetHitCounter(damagerUniqueId);

        this.setSpeed(damaged, -10, durationPerLevel[enchantLevel]);
        this.setJumpBoost(damaged, -10, durationPerLevel[enchantLevel]);

        damager.getWorld().playSound(damager.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 0.62f);
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

        this.attemptEnchantExecution(args);
    }
}

package me.keegan.enchantments;

import me.keegan.enums.cooldownEnums;
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
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.wordUtil.integerToOrdinal;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class ComboDamage extends enchantUtil {
    private final Integer[] damagePerLevel = new Integer[]{20, 30, 45};
    private final Integer[] hitsNeededPerLevel = new Integer[]{4, 3, 3};
    private final Integer durationUntilHitsReset = 15;

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
        return "Combo: Damage";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Every {1}{3}{0} strike deals/n"
                                + "{2}+{4}%{0} damage",
                        gray, yellow, red, integerToOrdinal(hitsNeededPerLevel[0]), damagePerLevel[0]),

                MessageFormat.format("{0}Every {1}{3}{0} strike deals/n"
                                + "{2}+{4}%{0} damage",
                        gray, yellow, red, integerToOrdinal(hitsNeededPerLevel[1]), damagePerLevel[1]),

                MessageFormat.format("{0}Every {1}{3}{0} strike deals/n"
                                + "{2}+{4}%{0} damage",
                        gray, yellow, red, integerToOrdinal(hitsNeededPerLevel[2]), damagePerLevel[2]),
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

        LivingEntity damager = (LivingEntity) e.getDamager();
        UUID damagerUniqueId = damager.getUniqueId();

        this.addHitToHitCounter(damagerUniqueId, 1);
        this.addCooldown(damagerUniqueId, (long) durationUntilHitsReset, cooldownEnums.RESET_HIT_COUNTER);

        if (this.getHitCounter(damagerUniqueId) < hitsNeededPerLevel[enchantLevel]) { return; }
        this.resetHitCounter(damagerUniqueId);

        playerDamageHandler.getInstance().addDamage(e, damagePerLevel[enchantLevel]);
        if (!(damager instanceof Player)) { return; }

        Player player = (Player) damager;
        player.playSound(player.getLocation(), Sound.ENTITY_DONKEY_HURT, 1f, 0.5f);
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

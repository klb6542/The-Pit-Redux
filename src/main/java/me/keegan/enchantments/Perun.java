package me.keegan.enchantments;

import me.keegan.enums.cooldownEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.wordUtil.integerToWord;
import me.keegan.handlers.playerDamageHandler;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Perun extends enchantUtil {
    private final Integer[] hitsNeeded = new Integer[]{5, 4, 4};
    private final Integer[] trueDamagePerLevel = new Integer[]{3, 4, 2};
    private Integer durationUntilHitsReset = 15;

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.GOLDEN_SWORD};
    }

    @Override
    public String getName() {
        return "Combo: Perun's Wrath";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Each {1}{4}{0} hit strikes/n" +
                        "{1}lighting{0} for {2}{5}❤{0}./n" +
                        "{0}{3}Lighting deals true damage", gray, yellow, red, italic,
                        integerToWord(hitsNeeded[0]), Math.floor((double) trueDamagePerLevel[0] / 2)),

                MessageFormat.format("{0}Each {1}{4}{0} hit strikes/n" +
                                "{1}lighting{0} for {2}{5}❤{0}./n" +
                                "{0}{3}Lighting deals true damage", gray, yellow, red, italic,
                        integerToWord(hitsNeeded[1]), Math.floor((double) trueDamagePerLevel[1] / 2)),

                MessageFormat.format("{0}Each {1}{4}{0} hit strikes/n" +
                                "{1}lighting{0} for {2}1❤ {0}+{2} {5}❤/n" +
                                "{0}per {6}enchanted piece {0}on/n" +
                                "{0}your victim./n" +
                                "{0}{3}Lighting deals true damage", gray, yellow, red, italic,
                        integerToWord(hitsNeeded[2]), Math.floor((double) trueDamagePerLevel[2] / 2), lightPurple),
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
    public void executeEnchant(Object... args) {
        int enchantLevel = (int) args[2];

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        LivingEntity damaged = (LivingEntity) e.getEntity();
        Player damager = (Player) e.getDamager();

        UUID damagerUniqueId = damager.getUniqueId();

        this.addHitToHitCounter(damagerUniqueId, 1);
        this.addCooldown(damagerUniqueId, (long) durationUntilHitsReset, cooldownEnums.RESET_HIT_COUNTER);

        if (this.getHitCounter(damagerUniqueId) >= hitsNeeded[enchantLevel]) {
            this.resetHitCounter(damagerUniqueId);

            // enchantLevel is 1 less than it actually is so that it can index arrays
            if (enchantLevel <= 1) {
                // perun 1 & 2
                playerDamageHandler.getInstance().addTrueDamage(e, trueDamagePerLevel[enchantLevel]);
            }else{
                // perun 3
                int count = 0;

                ItemStack[] armor
                        = (damaged.getEquipment().getArmorContents()) != null
                        ? damaged.getEquipment().getArmorContents()
                        : new ItemStack[]{};

                for (ItemStack armorPiece : armor) {
                    if (armorPiece.getEnchantments().isEmpty()) { continue; }

                    count++;
                }

                // multiply trueDamagePerLevel value by how many armor pieces have enchantment, then plus 1 heart
                playerDamageHandler.getInstance().addTrueDamage(e, (trueDamagePerLevel[enchantLevel] * count) + 2);
            }

            damaged.getWorld().strikeLightningEffect(damaged.getLocation());
        }
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

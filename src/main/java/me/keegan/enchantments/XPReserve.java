package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */


public class XPReserve extends enchantUtil {
    private final Integer[] maxReductionPerLevel = new Integer[]{15, 21, 30};
    private final Integer reductionAmountPerLevel = 1;
    private final Integer xpLevelsAmount = 5;

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
        return "XP Reserve";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Receive {1}-{3}% damage{0} per/n" +
                        "{2}{4} XP levels{0} you have ({1}-{5}%/n" +
                        "{0}max)", gray, blue, aqua, reductionAmountPerLevel,
                        xpLevelsAmount, maxReductionPerLevel[0]),

                MessageFormat.format("{0}Receive {1}-{3}% damage{0} per/n" +
                                "{2}{4} XP levels{0} you have ({1}-{5}%/n" +
                                "{0}max)", gray, blue, aqua, reductionAmountPerLevel,
                        xpLevelsAmount, maxReductionPerLevel[1]),

                MessageFormat.format("{0}Receive {1}-{3}% damage{0} per/n" +
                                "{2}{4} XP levels{0} you have ({1}-{5}%/n" +
                                "{0}max)", gray, blue, aqua, reductionAmountPerLevel,
                        xpLevelsAmount, maxReductionPerLevel[2])
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
        Player damaged = (Player) e.getEntity();

        int reductionAmount =
                (damaged.getLevel() % xpLevelsAmount == 0)
                ? damaged.getLevel() / xpLevelsAmount
                : (damaged.getLevel() - (damaged.getLevel() % xpLevelsAmount)) / xpLevelsAmount;

        reductionAmount *= reductionAmountPerLevel;

        // cap reduction amount
        reductionAmount = Math.max(1, Math.min(maxReductionPerLevel[enchantLevel], reductionAmount));

        playerDamageHandler.getInstance().reduceDamage(e, reductionAmount);
    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof LivingEntity)) { return; }

        Player damaged = (Player) e.getEntity();

        Object[] args = new Object[]{
                e,
                (damaged.getEquipment() != null) ? damaged.getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(damaged, args);
    }
}

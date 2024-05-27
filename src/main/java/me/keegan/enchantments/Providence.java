package me.keegan.enchantments;

import me.keegan.enums.cooldownEnums;
import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Providence extends enchantUtil {
    private final Integer[] duplicatePercentPerLevel = new Integer[]{20, 35, 50};
    private final Integer[] rowAmountNeededPerLevel = new Integer[]{Integer.MAX_VALUE, 4, 3};

    private final Integer durationUntilRowsReset = 30;

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
        return "Providence";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Killing entities has a {1}{2}% chance/n"
                                + "{0}to {1}duplicate drops{0}",
                        gray, green, duplicatePercentPerLevel[0]),

                MessageFormat.format("{0}Killing entities has a {1}{3}% chance/n"
                                + "{0}to {1}duplicate drops{0}. Receiving {1}{4}/n"
                                + "{0}in a row grants {2}2 duplicate drops",
                        gray, green, darkGreen, duplicatePercentPerLevel[1], rowAmountNeededPerLevel[1]),

                MessageFormat.format("{0}Killing entities has a {1}{3}% chance/n"
                        + "{0}to {1}duplicate drops{0}. Receiving {1}{4}/n"
                        + "{0}in a row grants {2}2 duplicate drops",
                        gray, green, darkGreen, duplicatePercentPerLevel[2], rowAmountNeededPerLevel[2])
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

        EntityDeathEvent e = (EntityDeathEvent) args[0];

        LivingEntity killed = e.getEntity();
        LivingEntity killer = killed.getKiller();
        UUID uuid = killer.getUniqueId();

        if (!this.shouldDuplicateDrops(enchantLevel)) { return; }

        // duplicate drops
        List<ItemStack> drops = e.getDrops();
        drops.forEach(itemStack -> e.getDrops().add(itemStack));

        this.addHitToHitCounter(uuid, 1);
        this.addCooldown(uuid, (long) durationUntilRowsReset, cooldownEnums.RESET_HIT_COUNTER);

        if (!this.getHitCounter(uuid).equals(rowAmountNeededPerLevel[enchantLevel])) { return; }

        // duplicate again
        drops.forEach(itemStack -> e.getDrops().add(itemStack));
        this.resetHitCounter(uuid);
    }

    private boolean shouldDuplicateDrops(int enchantLevel) {
        for (int i = 0; i < duplicatePercentPerLevel[enchantLevel]; i++) {
            if (new Random().nextInt(100) != 0) { continue; }

            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDied(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) { return; }
        Player killer = e.getEntity().getKiller();

        Object[] args = new Object[]{
                e,
                (killer.getEquipment() != null) ? killer.getEquipment().getLeggings() : null,
                this,
        };

        this.attemptEnchantExecution(killer, args);
    }
}

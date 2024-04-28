package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import java.text.MessageFormat;
import java.util.Random;

import static me.keegan.utils.formatUtil.*;

public class XPDuplex extends enchantUtil {
    private final Integer[] chancePerLevel = new Integer[]{50, 50, 65};
    private final Integer[] multiplierAmountPerLevel = new Integer[]{2, 3, 3};

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
        return "XP Duplex";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Killing entities has a {1}{2}%{0} chance to/n"
                        + "{0}drop {1}{3}x XP",
                        gray, aqua, chancePerLevel[0], multiplierAmountPerLevel[0]),

                MessageFormat.format("{0}Killing entities has a {1}{2}%{0} chance to/n"
                                + "{0}drop {1}{3}x XP",
                        gray, aqua, chancePerLevel[1], multiplierAmountPerLevel[1]),

                MessageFormat.format("{0}Killing entities has a {1}{2}%{0} chance to/n"
                                + "{0}drop {1}{3}x XP",
                        gray, aqua, chancePerLevel[2], multiplierAmountPerLevel[2])
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

        if (new Random().nextInt(100) > chancePerLevel[enchantLevel] - 1) { return; }
        e.setDroppedExp(e.getDroppedExp() * multiplierAmountPerLevel[enchantLevel]);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDied(EntityDeathEvent e) {
        LivingEntity killer = e.getEntity().getKiller();
        if (killer == null || killer.getEquipment() == null) { return; }

        Object[] args = new Object[]{
                e,
                (killer.getEquipment().getLeggings() != null) ? killer.getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(args);
    }
}

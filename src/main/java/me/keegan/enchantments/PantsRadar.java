package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.mysticHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

public class PantsRadar extends enchantUtil {
    private final Integer[] mysticDropChancePerLevel = new Integer[]{10, 20, 40};

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{
                Material.LEATHER_LEGGINGS,
                Material.GOLDEN_SWORD,
                Material.BOW
        };
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.NORMAL;
    }

    @Override
    public String getName() {
        return "Pants Radar";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Pants, golden swords, and enchanted/n" +
                        "{0}bows drop {1}+{2}%{0} more frequently",
                        gray, lightPurple, mysticDropChancePerLevel[0]),

                MessageFormat.format("{0}Pants, golden swords, and enchanted/n" +
                                "{0}bows drop {1}+{2}%{0} more frequently",
                        gray, lightPurple, mysticDropChancePerLevel[1]),

                MessageFormat.format("{0}Pants, golden swords, and enchanted/n" +
                                "{0}bows drop {1}+{2}%{0} more frequently",
                        gray, lightPurple, mysticDropChancePerLevel[2]),
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
        EntityDeathEvent e = (EntityDeathEvent) args[0];

        mysticHandler.mysticDrops.getInstance().addChance(e, mysticDropChancePerLevel[enchantLevel]);
    }

    @EventHandler
    public void entityDied(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) { return; }

        LivingEntity killer = e.getEntity().getKiller();
        EntityEquipment equipment = killer.getEquipment();
        if (equipment == null) { return; }

        // leggings check
        if (equipment.getLeggings() != null) {
            Object[] args = new Object[]{
                    e,
                    killer.getEquipment().getLeggings(),
                    this
            };

            this.attemptEnchantExecution(args);
        }

        // sword check
        if (equipment.getItemInMainHand().getType() == Material.GOLDEN_SWORD) {
            Object[] args = new Object[]{
                    e,
                    equipment.getItemInMainHand(),
                    this
            };

            this.attemptEnchantExecution(args);
        }

        // bow check
        if (equipment.getItemInMainHand().getType() == Material.BOW
                || equipment.getItemInOffHand().getType() == Material.BOW) {
            Object[] args = new Object[]{
                    e,
                    (equipment.getItemInMainHand().getType() == Material.BOW)
                            ? equipment.getItemInMainHand()
                            : equipment.getItemInOffHand(),
                    this
            };

            this.attemptEnchantExecution(args);
        }
    }
}

package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

public class SprintDrain extends enchantUtil {
    private final Integer[] speedDurationPerLevel = new Integer[]{3, 5, 7};
    private final Integer[] speedAmplifierPerLevel = new Integer[]{0, 0, 1};
    private final Integer[] slownessDurationPerLevel = new Integer[]{-1, 3, 3};
    private final Integer[] slownessAmplifierPerLevel = new Integer[]{-1, 0, 0};

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
        return "Sprint Drain";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Arrow shots grant you {1}Speed {2}/n"
                        + "{0}({3}s)", gray, yellow,
                        integerToRoman(speedAmplifierPerLevel[0] + 1, false), speedDurationPerLevel[0]),

                MessageFormat.format("{0}Arrow shots grant you {1}Speed {3}/n"
                                + "{0}({4}s) and apply {2}Slowness {5}/n"
                                + "{0}({6}s)", gray, yellow, blue,
                        integerToRoman(speedAmplifierPerLevel[1] + 1, false), speedDurationPerLevel[1],
                        integerToRoman(slownessAmplifierPerLevel[1] + 1, false), slownessDurationPerLevel[1]),

                MessageFormat.format("{0}Arrow shots grant you {1}Speed {3}/n"
                                + "{0}({4}s) and apply {2}Slowness {5}/n"
                                + "{0}({6}s)", gray, yellow, blue,
                        integerToRoman(speedAmplifierPerLevel[2] + 1, false), speedDurationPerLevel[2],
                        integerToRoman(slownessAmplifierPerLevel[2] + 1, false), slownessDurationPerLevel[2])
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
        Arrow arrow = (Arrow) e.getDamager();

        LivingEntity shooter = (LivingEntity) arrow.getShooter();
        LivingEntity shot = (LivingEntity) e.getEntity();

        this.setSpeed(shooter, speedAmplifierPerLevel[enchantLevel], speedDurationPerLevel[enchantLevel].doubleValue());

        if (slownessAmplifierPerLevel[enchantLevel] == -1) { return; }
        this.slowdown(shot, slownessAmplifierPerLevel[enchantLevel], slownessDurationPerLevel[enchantLevel].doubleValue());
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

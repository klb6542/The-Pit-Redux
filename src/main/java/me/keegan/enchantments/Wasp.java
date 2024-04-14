package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

public class Wasp extends enchantUtil {
    private final Integer[] weaknessAmplifierPerLevel = new Integer[]{1, 2, 3};
    private final Integer[] weaknessDurationPerLevel = new Integer[]{6, 11, 16};

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
        return "Wasp";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Apply {1}Weakness {2}{0} ({3}s) on hit", gray, red,
                        integerToRoman(weaknessAmplifierPerLevel[0] + 1, false), weaknessDurationPerLevel[0]),

                MessageFormat.format("{0}Apply {1}Weakness {2}{0} ({3}s) on hit", gray, red,
                        integerToRoman(weaknessAmplifierPerLevel[1] + 1, false), weaknessDurationPerLevel[1]),

                MessageFormat.format("{0}Apply {1}Weakness {2}{0} ({3}s) on hit", gray, red,
                        integerToRoman(weaknessAmplifierPerLevel[2] + 1, false), weaknessDurationPerLevel[2]),
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
        LivingEntity livingEntity = (LivingEntity) e.getEntity();

        this.addPotionEffect(livingEntity, PotionEffectType.WEAKNESS, weaknessAmplifierPerLevel[enchantLevel], weaknessDurationPerLevel[enchantLevel]);
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

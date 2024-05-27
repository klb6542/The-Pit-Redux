package me.keegan.enchantments;

import me.keegan.enums.cooldownEnums;
import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

public class Peroxide extends enchantUtil {
    private final Integer[] regenAmplifierPerLevel = new Integer[]{0, 0, 1};
    private final Integer[] regenDurationPerLevel = new Integer[]{4, 6, 4};
    private final Integer[] regenCooldownPerLevel = new Integer[]{6, 9, 7};

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
        return "Peroxide";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Gain {1}Regen {2}{0} ({3}s) when hit/n" +
                                "{0}({4}s cooldown)",
                        gray, red, integerToRoman(regenAmplifierPerLevel[0] + 1, false),
                        regenDurationPerLevel[0], regenCooldownPerLevel[0]),

                MessageFormat.format("{0}Gain {1}Regen {2}{0} ({3}s) when hit/n" +
                                "{0}({4}s cooldown)",
                        gray, red, integerToRoman(regenAmplifierPerLevel[1] + 1, false),
                        regenDurationPerLevel[1], regenCooldownPerLevel[1]),

                MessageFormat.format("{0}Gain {1}Regen {2}{0} ({3}s) when hit/n" +
                                "{0}({4}s cooldown)",
                        gray, red, integerToRoman(regenAmplifierPerLevel[2] + 1, false),
                        regenDurationPerLevel[2], regenCooldownPerLevel[2])
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
        LivingEntity damaged = (LivingEntity) e.getEntity();

        if (this.isOnCooldown(damaged.getUniqueId())) { return; }
        this.addCooldown(damaged.getUniqueId(), (long) regenCooldownPerLevel[enchantLevel], cooldownEnums.NORMAL);

        this.addPotionEffect(damaged, PotionEffectType.REGENERATION,
                regenAmplifierPerLevel[enchantLevel], (double) regenDurationPerLevel[enchantLevel]);
    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if ((!(e.getEntity() instanceof LivingEntity)
                || !(e.getDamager() instanceof LivingEntity))
                && !entityUtil.damagerIsArrow(e)) { return; }

        LivingEntity damaged = (LivingEntity) e.getEntity();

        Object[] args = new Object[]{
                e,
                (damaged.getEquipment() != null) ? damaged.getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(damaged, args);
    }
}

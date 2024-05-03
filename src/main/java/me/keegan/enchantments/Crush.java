package me.keegan.enchantments;

import me.keegan.enums.cooldownEnums;
import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

public class Crush extends enchantUtil {
    private final Integer[] weaknessAmplifierPerLevel = new Integer[]{5, 6, 7};
    private final Double[] weaknessDurationPerLevel = new Double[]{0.4, 0.6, 0.8};

    private final Integer weaknessCooldown = 2;

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
        return "Crush";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Strikes apply {1}Weakness {2}/n" +
                        "{0}(lasts {3}s, {4}s cooldown)",
                        gray, red, integerToRoman(weaknessAmplifierPerLevel[0], false),
                        weaknessDurationPerLevel[0], weaknessCooldown),

                MessageFormat.format("{0}Strikes apply {1}Weakness {2}/n" +
                                "{0}(lasts {3}s, {4}s cooldown)",
                        gray, red, integerToRoman(weaknessAmplifierPerLevel[1], false),
                        weaknessDurationPerLevel[1], weaknessCooldown),

                MessageFormat.format("{0}Strikes apply {1}Weakness {2}/n" +
                                "{0}(lasts {3}s, {4}s cooldown)",
                        gray, red, integerToRoman(weaknessAmplifierPerLevel[2], false),
                        weaknessDurationPerLevel[2], weaknessCooldown)
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
        LivingEntity damager = (LivingEntity) e.getDamager();

        if (this.isOnCooldown(damager.getUniqueId())) { return; }
        this.addCooldown(damager.getUniqueId(), weaknessCooldown.longValue(), cooldownEnums.NORMAL);

        if (damager instanceof Player) {
            ((Player) damager).playSound(damager.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.25f, 0.65f);
        }

        this.addPotionEffect(damaged, PotionEffectType.WEAKNESS,
                weaknessAmplifierPerLevel[enchantLevel], weaknessDurationPerLevel[enchantLevel]);
    }

    @EventHandler()
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

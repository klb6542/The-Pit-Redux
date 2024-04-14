package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

public class PainFocus extends enchantUtil {
    private final Integer[] damagePerLevel = new Integer[]{3, 4, 6};

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
        return "Pain Focus";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Deal {1}+{2}%{0} damage per {1}❤/n" +
                                "{0}you''re missing",
                        gray, red, damagePerLevel[0]),

                MessageFormat.format("{0}Deal {1}+{2}%{0} damage per {1}❤/n" +
                                "{0}you''re missing",
                        gray, red, damagePerLevel[1]),

                MessageFormat.format("{0}Deal {1}+{2}%{0} damage per {1}❤/n" +
                                "{0}you''re missing",
                        gray, red, damagePerLevel[2])
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

        int damage = (int)
                (Math.abs(damager.getHealth()
                - damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
                / 2;

        playerDamageHandler.getInstance().addDamage(e, damage * damagePerLevel[enchantLevel]);
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

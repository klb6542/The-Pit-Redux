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

import static me.keegan.utils.formatUtil.gray;
import static me.keegan.utils.formatUtil.red;

public class Punisher extends enchantUtil {
    private final Integer[] damagePerLevel = new Integer[]{13, 16, 21};

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
        return "Punisher";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Deal {1}+{2}%{0} damage vs. entities/n"
                        + "{0}below 50% HP", gray, red, damagePerLevel[0]),

                MessageFormat.format("{0}Deal {1}+{2}%{0} damage vs. entities/n"
                        + "{0}below 50% HP", gray, red, damagePerLevel[1]),

                MessageFormat.format("{0}Deal {1}+{2}%{0} damage vs. entities/n"
                        + "{0}below 50% HP", gray, red, damagePerLevel[2])
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

        if (damaged.getHealth() > (damaged.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2)) { return; }
        playerDamageHandler.getInstance().addDamage(e, damagePerLevel[enchantLevel]);
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

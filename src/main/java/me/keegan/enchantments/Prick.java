package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

public class Prick extends enchantUtil {
    private final Double[] trueDamagePerLevel = new Double[]{0.25, 0.5, 1.0};

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
        return "Prick";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Enemies hitting you receive {1}{2}❤/n"
                        + "{0}true damage",
                        gray, red, trueDamagePerLevel[0] / 2),

                MessageFormat.format("{0}Enemies hitting you receive {1}{2}❤/n"
                                + "{0}true damage",
                        gray, red, trueDamagePerLevel[1] / 2),

                MessageFormat.format("{0}Enemies hitting you receive {1}{2}❤/n"
                                + "{0}true damage",
                        gray, red, trueDamagePerLevel[2] / 2)
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

        if (damager instanceof Player) {
            ((Player) damager).playSound(damaged.getLocation(), Sound.ENCHANT_THORNS_HIT, 1f, 1.25f);
        }

        playerDamageHandler.getInstance().doTrueDamage(damager, trueDamagePerLevel[enchantLevel]);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if ((!(e.getEntity() instanceof LivingEntity)
                || !(e.getDamager() instanceof LivingEntity))) { return; }

        LivingEntity damaged = (LivingEntity) e.getEntity();

        Object[] args = new Object[]{
                e,
                (damaged.getEquipment() != null) ? damaged.getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(args);
    }
}

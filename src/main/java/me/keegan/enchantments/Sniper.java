package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

public class Sniper extends enchantUtil {
    private final Integer[] damagePerLevel = new Integer[]{18, 25, 32};
    private final Integer[] blockAwayMinimumPerLevel = new Integer[]{25, 24, 24};

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
        return "Sniper";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Deal {1}+{3}%{0} damage when shooting/n"
                        + "{0}from over {2}{4}{0} blocks", gray, red, white,
                        damagePerLevel[0], blockAwayMinimumPerLevel[0]),

                MessageFormat.format("{0}Deal {1}+{3}%{0} damage when shooting/n"
                                + "{0}from over {2}{4}{0} blocks", gray, red, white,
                        damagePerLevel[1], blockAwayMinimumPerLevel[2]),

                MessageFormat.format("{0}Deal {1}+{3}%{0} damage when shooting/n"
                                + "{0}from over {2}{4}{0} blocks", gray, red, white,
                        damagePerLevel[1], blockAwayMinimumPerLevel[2])
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
        LivingEntity damager = (LivingEntity) ((Arrow) e.getDamager()).getShooter();
        if (this.getDistance(damaged, damager) < blockAwayMinimumPerLevel[enchantLevel]) { return; }
        ThePitRedux.getPlugin().getLogger().info("Distance = " + this.getDistance(damaged, damager));

        playerDamageHandler.getInstance().addDamage(e, damagePerLevel[enchantLevel]);
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

        this.attemptEnchantExecution(shooter, args);
    }
}

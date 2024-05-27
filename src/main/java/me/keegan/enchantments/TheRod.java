package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.gray;

public class TheRod extends enchantUtil {
    private final Integer knockbackMultiplier = 5;

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.LEATHER_LEGGINGS};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.AQUA;
    }

    @Override
    public String getName() {
        return "The Rod";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Fishing rods deal increased/n"
                        + "{0}knockback", gray)
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
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        LivingEntity damaged = (LivingEntity) e.getEntity();
        LivingEntity damager = (LivingEntity) e.getDamager();

        if (damager.getEquipment().getItemInMainHand().getType() != Material.FISHING_ROD) { return; }
        damaged.setVelocity(damager.getLocation().getDirection().setY(0).normalize().multiply(knockbackMultiplier));
    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if ((!(e.getEntity() instanceof LivingEntity) || !(e.getDamager() instanceof LivingEntity))) { return; }

        LivingEntity damager = (LivingEntity) e.getDamager();

        Object[] args = new Object[]{
                e,
                (damager.getEquipment() != null) ? damager.getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(damager, args);
    }
}

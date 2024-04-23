package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

public class Explosive extends enchantUtil {
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
        return "Explosive";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{};
    }

    @Override
    public Integer getMaxLevel() {
        return this.getEnchantDescription().length;
    }

    @Override
    public boolean isRareEnchant() {
        return true;
    }

    @Override
    public boolean isMysticWellEnchant() {
        return true;
    }

    @Override
    public void executeEnchant(Object[] args) {

    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent e) {
        if (!entityUtil.projectileIsArrow(e)) { return; }
        LivingEntity shooter = (LivingEntity) e.getEntity().getShooter();

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

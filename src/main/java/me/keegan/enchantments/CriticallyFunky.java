package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.UUID;

public class CriticallyFunky extends enchantUtil {
    private static final HashMap<UUID, Integer> damageEmpowerment = new HashMap<>();
    private final Integer[] reducedDamagePerLevel = new Integer[]{20, 35, 50};
    private final Integer[] damageEmpowermentPerLevel = new Integer[]{0, 14, 30};

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
        return "Critically Funky";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                //MessageFormat.format("")
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
        LivingEntity damager = (LivingEntity) e.getDamager();

        if (!this.isCriticalHit(damager)) { return; }
        int enchantLevel = (int) args[2];

        damageEmpowerment.put(damager.getUniqueId(), damageEmpowermentPerLevel[enchantLevel]);
        playerDamageHandler.getInstance().reduceDamage(e, enchantLevel);
    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity) || !(e.getDamager() instanceof LivingEntity)) { return; }
        LivingEntity damaged = (LivingEntity) e.getEntity();

        Object[] args = new Object[]{
                e,
                (damaged.getEquipment() != null) ? damaged.getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(damaged, args);
    }
}

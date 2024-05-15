package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;

public class Grasshopper extends enchantUtil {
    private final List<Material> grassTypes = new ArrayList<Material>(){{
       add(Material.GRASS_BLOCK);
       add(Material.GRASS_PATH);
       add(Material.DIRT);
       add(Material.COARSE_DIRT);
       add(Material.MYCELIUM);
    }};

    private final Integer[] damagePerLevel = new Integer[]{10, 17, 25};

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
        return "Grasshopper";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Deal {1}+{2}%{0} damage when you or/n"
                        + "{0}your victim are standing on grass",
                        gray, red, damagePerLevel[0]),

                MessageFormat.format("{0}Deal {1}+{2}%{0} damage when you or/n"
                                + "{0}your victim are standing on grass",
                        gray, red, damagePerLevel[1]),

                MessageFormat.format("{0}Deal {1}+{2}%{0} damage when you or/n"
                                + "{0}your victim are standing on grass",
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
        LivingEntity damaged = (LivingEntity) e.getEntity();

        if (!this.isStandingOnGrass(damager) && !this.isStandingOnGrass(damaged)) { return; }
        playerDamageHandler.getInstance().reduceDamage(e, damagePerLevel[enchantLevel]);
    }

    private boolean isStandingOnGrass(LivingEntity livingEntity) {
        return grassTypes.contains(livingEntity.getLocation().getBlock().getType());
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

package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import me.keegan.handlers.playerDamageHandler;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import static me.keegan.utils.formatUtil.*;

public class Shark extends enchantUtil {
    private final Integer[] damagePerLevel = new Integer[]{3, 5, 7};

    private final Integer healthMaxToProc = 12;
    private final Integer sharkBlockRadius = 12;

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
        return "Shark";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Deal {1}+{2}%{0} damage per other/n" +
                        "{0}entity below {1}{3}❤{0} within {4}/n" +
                        "{0}blocks", gray, red, damagePerLevel[0],
                        (double) healthMaxToProc / 2, sharkBlockRadius),

                MessageFormat.format("{0}Deal {1}+{2}%{0} damage per other/n" +
                        "{0}entity below {1}{3}❤{0} within {4}/n" +
                        "{0}blocks", gray, red, damagePerLevel[1],
                        (double) healthMaxToProc / 2, sharkBlockRadius),

                MessageFormat.format("{0}Deal {1}+{2}%{0} damage per other/n" +
                        "{0}entity below {1}{3}❤{0} within {4}/n" +
                        "{0}blocks", gray, red, damagePerLevel[2],
                        (double) healthMaxToProc / 2, sharkBlockRadius)
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
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        LivingEntity damager = (LivingEntity) e.getDamager();

        List<LivingEntity> livingEntityList = damager.getNearbyEntities(sharkBlockRadius, sharkBlockRadius, sharkBlockRadius)
                .stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(livingEntity -> livingEntity.getHealth() < healthMaxToProc)
                .collect(Collectors.toList());

        playerDamageHandler.getInstance().addDamage(e,
                livingEntityList.size() * damagePerLevel[enchantLevel]);
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

package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.wordUtil.integerToWord;

public class Solitude extends enchantUtil {
    private final Integer[] maxEntitiesPerLevel = new Integer[]{2, 3, 3};
    private final Integer[] reducedDamagePerLevel = new Integer[]{20, 40, 65};

    private final Integer solitudeBlockRadius = 6;

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
        return "Solitude";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Receive {1}-{2}%{0} damage when {3}/n" +
                        "{0}or less entities are within {4}/n" +
                        "{0}blocks", gray, blue, reducedDamagePerLevel[0],
                        integerToWord(maxEntitiesPerLevel[0]), solitudeBlockRadius),

                MessageFormat.format("{0}Receive {1}-{2}%{0} damage when {3}/n" +
                                "{0}or less entities are within {4}/n" +
                                "{0}blocks", gray, blue, reducedDamagePerLevel[1],
                        integerToWord(maxEntitiesPerLevel[1]), solitudeBlockRadius),

                MessageFormat.format("{0}Receive {1}-{2}%{0} damage when {3}/n" +
                                "{0}or less entities are within {4}/n" +
                                "{0}blocks", gray, blue, reducedDamagePerLevel[2],
                        integerToWord(maxEntitiesPerLevel[2]), solitudeBlockRadius)
        };
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
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        LivingEntity damaged = (LivingEntity) e.getEntity();

        List<LivingEntity> nearbyLivingEntities = damaged.getNearbyEntities(solitudeBlockRadius, solitudeBlockRadius, solitudeBlockRadius)
                .stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .collect(Collectors.toList());

        if (nearbyLivingEntities.size() > this.maxEntitiesPerLevel[enchantLevel]) { return; }

        playerDamageHandler.getInstance().reduceDamage(e, reducedDamagePerLevel[enchantLevel]);
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

        this.attemptEnchantExecution(args);
    }
}
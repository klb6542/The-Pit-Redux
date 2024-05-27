package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.setupUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class WolfPack extends enchantUtil implements setupUtils {
    private final static List<Wolf> currentWolves = new ArrayList<>();

    private final Integer[] killsNeededPerLevel = new Integer[]{4, 4, 3};
    private final Integer[] durationPerLevel = new Integer[]{30, 60, 90};

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
        return "Wolf Pack";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Spawn a {1}Wolf {0}every {2} kills./n"
                                + "{1}Wolves{0} despawn after {1}{3}{0}/n"
                                + "{0}seconds",
                        gray, red, killsNeededPerLevel[0], durationPerLevel[0]),

                MessageFormat.format("{0}Spawn a {1}Wolf {0}every {2} kills./n"
                                + "{1}Wolves{0} despawn after {1}{3}{0}/n"
                                + "{0}seconds",
                        gray, red, killsNeededPerLevel[1], durationPerLevel[1]),

                MessageFormat.format("{0}Spawn a {1}Wolf {0}every {2} kills./n"
                                + "{1}Wolves{0} despawn after {1}{3}{0}/n"
                                + "{0}seconds",
                        gray, red, killsNeededPerLevel[2], durationPerLevel[2])
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
    public boolean isMysticWellEnchant() {
        return true;
    }

    @Override
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];

        EntityDeathEvent e = (EntityDeathEvent) args[0];
        LivingEntity killed = e.getEntity();
        LivingEntity killer = killed.getKiller();
        UUID uuid = killer.getUniqueId();

        this.addHitToHitCounter(uuid, 1);
        if (!this.getHitCounter(uuid).equals(killsNeededPerLevel[enchantLevel])) { return; }
        this.resetHitCounter(uuid);

        Wolf wolf = (Wolf) killed.getWorld().spawnEntity(killed.getLocation(), EntityType.WOLF);
        wolf.setOwner((AnimalTamer) killer);

        wolf.getWorld().playSound(wolf.getLocation(), Sound.ENTITY_WOLF_AMBIENT, 1, 1);
        currentWolves.add(wolf);

        new BukkitRunnable() {
            final int[] count = new int[]{-1};
            final int duration = durationPerLevel[enchantLevel];

            @Override
            public void run() {
                if (wolf.isDead() || count[0] == duration) { removeWolf(wolf, this); return; }

                count[0]++;
                wolf.setCustomName(gray.toString() + (duration - count[0]));
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 20, 20);
    }

    private void removeWolf(Wolf wolf, BukkitRunnable bukkitRunnable) {
        if (!wolf.isDead()) {
            wolf.remove();
        }

        bukkitRunnable.cancel();
        currentWolves.remove(wolf);
    }

    @EventHandler
    public void entityDied(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) { return; }
        Player killer = e.getEntity().getKiller();

        Object[] args = new Object[]{
                e,
                (killer.getEquipment() != null) ? killer.getEquipment().getLeggings() : null,
                this,
        };

        this.attemptEnchantExecution(killer, args);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        currentWolves.forEach(Wolf::remove);
    }
}

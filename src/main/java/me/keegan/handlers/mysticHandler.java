package me.keegan.handlers;

import me.keegan.builders.mystic;
import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.mysticUtil;
import me.keegan.utils.propertiesUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.keegan.utils.itemUtil.dyes;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class mysticHandler implements Listener {
    private boolean containsDye(CraftingInventory craftingInventory) {
        // https://docs.oracle.com/javase/tutorial/essential/concurrency/atomicvars.html
        AtomicBoolean isDye = new AtomicBoolean(false);

        // loop through each item in inventory, if it is a dye then set boolean to true and keep it true
        craftingInventory.forEach(itemStack -> {
            if (itemStack != null) {
                isDye.set(isDye.get() || dyes.contains(itemStack.getType()));
            }
        });

        return isDye.get();
    }

    private boolean containsNotCraftable(CraftingInventory craftingInventory) {
        AtomicBoolean containsNotCraftable = new AtomicBoolean(false);

        craftingInventory.forEach(itemStack -> {
            if (itemStack != null) {
                containsNotCraftable.set(
                        containsNotCraftable.get()
                                || propertiesUtil.hasProperty(propertiesUtil.notCraftable, itemStack.getItemMeta()));
            }
        });

        return containsNotCraftable.get();
    }

    @EventHandler
    public void prepareAnvil(PrepareAnvilEvent e) {
        if (mysticUtil.getInstance().isMystic(e.getResult())) { e.setResult(new ItemStack(Material.AIR)); }
    }

    @EventHandler
    public void prepareItemEnchant(PrepareItemEnchantEvent e) {
        e.setCancelled(mysticUtil.getInstance().isMystic(e.getItem()));
    }

    @EventHandler
    public void craftItem(CraftItemEvent e) {
        e.setCancelled((mysticUtil.getInstance().isMystic(e.getCurrentItem()) && containsDye(e.getInventory()))
                || containsNotCraftable(e.getInventory()));
    }

    @EventHandler
    public void furnaceBurn(FurnaceBurnEvent e) {
        e.setCancelled(propertiesUtil.hasProperty(propertiesUtil.notBurnable, e.getFuel().getItemMeta()));
    }

    @EventHandler
    public void blockPlaced(BlockPlaceEvent e) {
        if (e.getItemInHand().getItemMeta() == null) { return; }

        e.setCancelled(propertiesUtil.hasProperty(propertiesUtil.notPlaceable, e.getItemInHand().getItemMeta()));
    }

    // FRESH MYSTIC DROP HANDLER
    public static class mysticDrops implements Listener {
        private static mysticDrops instance;
        private final HashMap<EntityDeathEvent, Integer> mysticDropChances = new HashMap<>();
        private final Integer defaultMysticDropChance = 750; // 1 out of 750 (0.00133333%)

        public static mysticDrops getInstance() {
            if (instance == null) {
                instance = new mysticDrops();
            }

            return instance;
        }

        public void addChance(EntityDeathEvent e, Integer chanceAmount) {
            this.mysticDropChances.put(e, chanceAmount + this.mysticDropChances.getOrDefault(e, 1));
        }

        public void resetChance(EntityDeathEvent e) {
            this.mysticDropChances.remove(e);
        }

        private Boolean shouldDropMystic(EntityDeathEvent e) {
            Integer mysticDropChance = instance.mysticDropChances.getOrDefault(e, 1);

            for (int i = 0; i < mysticDropChance; i++) {
                if (new Random().nextInt(defaultMysticDropChance) != 0) { continue; }

                return true;
            }

            return false;
        }

        private ItemStack createMysticDrop() {
            switch (new Random().nextInt(3)) {
                case 0:
                    return new mystic.Builder()
                            .material(Material.GOLDEN_SWORD)
                            .type(mysticEnums.NORMAL)
                            .build();
                case 1:
                    return new mystic.Builder()
                            .material(Material.BOW)
                            .type(mysticEnums.NORMAL)
                            .build();
                case 2:
                    int randomInteger = new Random().nextInt(mysticUtil.getInstance().defaultPantsColors.size());

                    Color pantsColor = mysticUtil.getInstance().defaultPantsColors.get(randomInteger);
                    ChatColor pantsChatColor = mysticUtil.getInstance().defaultPantsChatColors.get(randomInteger);

                    return new mystic.Builder()
                            .material(Material.LEATHER_LEGGINGS)
                            .type(mysticEnums.NORMAL)
                            .color(pantsColor)
                            .chatColor(pantsChatColor)
                            .build();
                default:
                    return new ItemStack(Material.AIR);
            }
        }

        private void playMainSound(LivingEntity killed) {
            new BukkitRunnable() {
                int count = 0;
                float soundPitch = 1.5f;

                @Override
                public void run() {
                    killed.getWorld().playSound(killed.getKiller().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5f, soundPitch);
                    soundPitch += 0.5f;
                    count++;

                    if (count != 5) { return; }
                    this.cancel();
                }

            }.runTaskTimer(ThePitRedux.getPlugin(), 0, 3);
        }

        private void playBackgroundSound(LivingEntity killed) {
            new BukkitRunnable() {
                int count = 0;
                float soundPitch = 0.1f;

                @Override
                public void run() {
                    killed.getWorld().playSound(killed.getKiller().getLocation(), Sound.ENTITY_ITEM_PICKUP, 2f, soundPitch);
                    soundPitch += 0.2f;
                    count++;

                    if (count != 15) { return; }
                    this.cancel();
                }

            }.runTaskTimer(ThePitRedux.getPlugin(), 1, 0);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void entityDied(EntityDeathEvent e) {
            LivingEntity killed = e.getEntity();

            if (killed.getKiller() != null && mysticDrops.getInstance().shouldDropMystic(e)) {
                Location location = killed.getLocation();
                location.add(0, 1, 0);

                killed.getWorld().dropItem(location, instance.createMysticDrop());
                killed.getWorld().spawnParticle(Particle.WATER_SPLASH, location, 200, 0.14, 0.275, 0.14); // i = amount

                instance.playMainSound(killed);
                instance.playBackgroundSound(killed);
            }

            mysticDrops.getInstance().resetChance(e);
        }
    }
}
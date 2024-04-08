package me.keegan.mysticwell;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static me.keegan.utils.itemUtil.dyes;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class animation {
    private final Inventory inventory;
    private final List<ItemStack> glassPanes;

    private final List<BukkitTask> runnableTasks = new ArrayList<>();

    // itemstack is mystic currently being enchanted
    protected ItemStack itemStack = new ItemStack(Material.AIR);
    protected boolean isEnchanting = false;
    protected boolean isEnchantingComplete = false;

    animation(Inventory inventory, List<ItemStack> glassPanes) {
        this.inventory = inventory;
        this.glassPanes = glassPanes;

        this.idleState();
    }

    protected void cancelRunnableTasks() {
        while (!this.runnableTasks.isEmpty()) {
            for (int i = 0; i < this.runnableTasks.size(); i++) {
                if (!this.runnableTasks.get(i).isCancelled()){
                    this.runnableTasks.get(i).cancel();
                }

                this.runnableTasks.remove(this.runnableTasks.get(i));
            }
        }
    }

    private void cooldown(int duration, CompletableFuture<Boolean> completableFuture) {
        this.runnableTasks.add(new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;
                if (counter < duration * 20) { return; }

                completableFuture.complete(true);
                this.cancel();
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 1, 1));
    }

    private void setGlassPanesColor(Material material) {
        for (ItemStack glassPane : this.glassPanes) {
            glassPane.setType(material);
        }
    }

    // creates scrambled dyes in the mystic slot
    private void scrambleState(int delay, int period) {
        Inventory runnableInventory = this.inventory;

        this.runnableTasks.add(new BukkitRunnable() {

            @Override
            public void run() {
                runnableInventory.setItem(20, new ItemStack(dyes.get(new Random().nextInt(dyes.size()))));
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), delay, period));
    }

    protected void idleState() {
        this.cancelRunnableTasks();
        this.setGlassPanesColor(Material.BLACK_STAINED_GLASS_PANE);
        this.isEnchantingComplete = false;

        ItemStack[] glassPanesArray = this.glassPanes.toArray(new ItemStack[0]);

        this.runnableTasks.add(new BukkitRunnable() {
            // j represents the index that was before i
            int i = 0;
            int j = 0;

            @Override
            public void run() {
                glassPanesArray[j].setType(Material.BLACK_STAINED_GLASS_PANE);
                glassPanesArray[i].setType(Material.PINK_STAINED_GLASS_PANE);

                j = i;
                i = (i == glassPanesArray.length - 1) ? 0 : i + 1;
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, 3));
    }

    protected void enchantingState() {
        this.cancelRunnableTasks();
        this.isEnchanting = true;

        final List<ItemStack> glassPanesList = this.glassPanes;

        // mystic can not be null/empty because it has been checked beforehand
        Inventory runnableInventory = this.inventory;
        itemStack = this.inventory.getItem(20).clone();

        // https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html
        CompletableFuture<Boolean> phaseOneCompletable = new CompletableFuture<>();
        CompletableFuture<Boolean> phaseTwoCompletable = new CompletableFuture<>();

        this.runnableTasks.add(new BukkitRunnable() {
            int stage = 0;

            @Override
            public void run() {
                switch (stage) {
                    case 0:
                    case 2:
                        glassPanesList.forEach(glassPane -> glassPane.setType(Material.RED_STAINED_GLASS_PANE));
                        runnableInventory.setItem(20, new ItemStack(Material.RED_STAINED_GLASS_PANE));
                        break;
                    case 1:
                        glassPanesList.forEach(glassPane -> glassPane.setType(Material.BLACK_STAINED_GLASS_PANE));
                        runnableInventory.setItem(20, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
                        break;
                    case 3:
                        glassPanesList.forEach(glassPane -> glassPane.setType(Material.BLACK_STAINED_GLASS_PANE));
                        runnableInventory.setItem(20, new ItemStack(Material.AIR));
                }

                stage++;
                if (stage < 4) { return; }

                phaseOneCompletable.complete(true);
                this.cancel();
            }

        }.runTaskTimerAsynchronously(ThePitRedux.getPlugin(), 0, 4));

        phaseOneCompletable.thenRun(() -> {
            this.idleState();
            this.scrambleState(0, 2);
            this.cooldown(4, phaseTwoCompletable);
        });

        phaseTwoCompletable.thenRun(() -> {
            this.cancelRunnableTasks();
            this.setGlassPanesColor(Material.RED_STAINED_GLASS_PANE);

            this.isEnchanting = false;
            this.isEnchantingComplete = true;

            this.inventory.setItem(20, itemStack);
        });
    }
}

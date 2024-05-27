package me.keegan.mysticwell;

import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.mysticUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static me.keegan.utils.formatUtil.red;
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
    protected Material glassPaneMaterial = Material.PINK_STAINED_GLASS_PANE;
    protected boolean isEnchanting = false;
    protected boolean isEnchantingComplete = false;
    protected boolean isAllowedToEnchant = false;
    protected Player player;

    animation(Inventory inventory, List<ItemStack> glassPanes, Player player) {
        this.inventory = inventory;
        this.glassPanes = glassPanes;
        this.player = player;

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

    private boolean isEnchantingSlotDye() {
        ItemStack slotItemStack = this.inventory.getItem(25);

        return slotItemStack != null && slotItemStack.getType().getKey().toString().contains("dye");
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
    private void scrambleState() {
        Inventory runnableInventory = this.inventory;
        final animation thisAnimation = this;

        this.runnableTasks.add(new BukkitRunnable() {

            @Override
            public void run() {
                runnableInventory.setItem(20, new ItemStack(dyes.get(new Random().nextInt(dyes.size()))));
                mysticWell.playCustomPitchSound(thisAnimation.player, (new Random().nextInt(11 - 9) / 10f) + 1f);
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, 2));
    }

    private void idleMaxState() {
        if (this.isEnchanting) { return; }
        this.isAllowedToEnchant = false;

        ItemStack redConcrete = new ItemStack(Material.RED_CONCRETE);
        ItemMeta itemMeta = redConcrete.getItemMeta();

        itemMeta.setDisplayName(red + "Maxed out!");
        redConcrete.setItemMeta(itemMeta);

        this.inventory.setItem(25, redConcrete);
    }

    protected void resetIdleMaxState() {
        this.isAllowedToEnchant = true;
        this.itemStack = new ItemStack(Material.AIR);

        this.inventory.setItem(25, new mysticWell().createItem());
    }

    protected void idleState() {
        this.cancelRunnableTasks();
        this.setGlassPanesColor(Material.BLACK_STAINED_GLASS_PANE);

        this.isEnchantingComplete = false;
        this.isAllowedToEnchant = true;

        ItemStack[] glassPanesArray = this.glassPanes.toArray(new ItemStack[0]);

        final Player thisPlayer = this.player;

        this.runnableTasks.add(new BukkitRunnable() {
            // j represents the index that is before i
            int i = 0;
            int j = 0;

            @Override
            public void run() {
                animation animation = mysticWell.animations.get(thisPlayer.getUniqueId());

                if (mysticUtil.getInstance().getTier(animation.itemStack) >= mysticUtil.getInstance().getMaxTier(itemStack)) {
                    animation.idleMaxState();
                }

                glassPanesArray[j].setType(Material.BLACK_STAINED_GLASS_PANE);
                glassPanesArray[i].setType(animation.glassPaneMaterial);

                j = i;
                i = (i == glassPanesArray.length - 1) ? 0 : i + 1;

                if (isEnchantingSlotDye() && i == 0) {

                }
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, 3));
    }

    protected void enchantingState() {
        this.cancelRunnableTasks();
        this.isEnchanting = true;

        // mystic can not be null/empty because it has been checked beforehand
        Inventory runnableInventory = this.inventory;
        this.itemStack = this.inventory.getItem(20).clone();

        this.glassPaneMaterial = mysticUtil.getInstance().getItemStackTierGlassPane(
                this.itemStack,
                mysticUtil.getInstance().getTier(this.itemStack) + 1
        );

        // finish enchanting before animation plays
        algorithm algorithm = new algorithm(this.player, this.itemStack);
        algorithm.run();

        final List<ItemStack> glassPanesList = this.glassPanes;
        final animation thisAnimation = this;

        // https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html
        CompletableFuture<Boolean> phaseOneCompletable = new CompletableFuture<>();
        CompletableFuture<Boolean> phaseTwoCompletable = new CompletableFuture<>();

        this.runnableTasks.add(new BukkitRunnable() {
            int stage = 0;

            @Override
            public void run() {
                switch (stage) {
                    case 0:
                        mysticWell.playLowPitchSound(player);
                    case 2:
                        glassPanesList.forEach(glassPane -> glassPane.setType(thisAnimation.glassPaneMaterial));
                        runnableInventory.setItem(20, new ItemStack(thisAnimation.glassPaneMaterial));

                        if (stage == 2) {
                            mysticWell.playHighPitchSound(player);
                        }

                        break;
                    case 1:
                        glassPanesList.forEach(glassPane -> glassPane.setType(Material.BLACK_STAINED_GLASS_PANE));
                        runnableInventory.setItem(20, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
                        break;
                    case 3:
                        glassPanesList.forEach(glassPane -> glassPane.setType(Material.BLACK_STAINED_GLASS_PANE));
                        runnableInventory.setItem(20, null);
                        break;
                    case 4: // make it last longer
                        break;
                }

                stage++;
                if (stage < 5) { return; }

                phaseOneCompletable.complete(true);
                this.cancel();
            }

        }.runTaskTimerAsynchronously(ThePitRedux.getPlugin(), 0, 4));

        phaseOneCompletable.thenRun(() -> {
            this.idleState();
            this.scrambleState();
            this.cooldown(4, phaseTwoCompletable);
        });

        phaseTwoCompletable.thenRun(() -> {
            this.cancelRunnableTasks();
            this.setGlassPanesColor(this.glassPaneMaterial);

            this.isEnchanting = false;
            this.isEnchantingComplete = true;

            this.inventory.setItem(20, this.itemStack);

            if (mysticUtil.getInstance().getTier(this.itemStack) == 2) {
                mysticWell.displayPantsSlot(this.inventory);
            }

            if (mysticUtil.getInstance().getTier(this.itemStack) >= mysticUtil.getInstance().getMaxTier(this.itemStack)) {
                this.idleMaxState();
                return;
            }

            mysticWell.toggleEnchantCostDisplay(this.player, this.inventory, true);
        });
    }
}

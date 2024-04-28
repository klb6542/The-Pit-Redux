package me.keegan.events.nightquest;

/*
 * Copyright (c) 2024. Created by klb.
 */

import me.keegan.items.vile.vile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class nightQuestModel {

    /*
     * The required progress is the total number of anything until
     * the night quest can be completed.
     *
     * The progress is the current number that the player is on
     * until the night quest can be completed.
     */

    private final nightQuestEnums nightQuestType;
    private final int requiredProgress;
    private final Player player;
    private int progress = 0;
    private final Object target;

    nightQuestModel(Player player, nightQuestEnums nightQuestType, Object target, int requiredProgress) {
        this.requiredProgress = requiredProgress;
        this.nightQuestType = nightQuestType;
        this.player = player;
        this.target = target;
    }

    int getProgress() {
        return this.progress;
    }

    int getRequiredProgress() {
        return this.requiredProgress;
    }

    Player getPlayer() {
        return this.player;
    }

    Object getTarget() {
        return this.target;
    }

    nightQuestEnums getNightQuestType() {
        return this.nightQuestType;
    }

    void addProgress(int progressAmount) {
        this.progress += progressAmount;
    }

    void nightQuestComplete() {
        PlayerInventory playerInventory = this.getPlayer().getInventory();
        ItemStack vile = new vile().createItem();

        // inv full
        if (playerInventory.firstEmpty() == -1) {
            this.player.getWorld().dropItem(this.player.getLocation(), vile);
            return;
        }

        playerInventory.addItem(vile);
    }
}

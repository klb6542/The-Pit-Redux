package me.keegan.events.nightquest;

/*
 * Copyright (c) 2024. Created by klb.
 */

import me.keegan.items.vile.vile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Random;

import static me.keegan.utils.formatUtil.*;

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

    int getProgress() { return this.progress; }

    int getRequiredProgress() {
        return this.requiredProgress;
    }

    Player getPlayer() {
        return this.player;
    }

    Object getTarget() {
        return this.target;
    }

    String getTargetName() {
        return target.toString().replace("_", " ");
    }

    nightQuestEnums getNightQuestType() {
        return this.nightQuestType;
    }

    void addProgress(int progressAmount) {
        this.progress += progressAmount;

        if (Math.floor((double) this.getRequiredProgress() / 2) != (double) this.progress) { return; }

        this.player.sendMessage(blue + "" + bold + "NIGHT QUEST! "
                + gray + "Half way complete! " + blue +  "(" + this.progress + "/" + this.requiredProgress + ")");
    }

    void nightQuestComplete() {
        PlayerInventory playerInventory = this.getPlayer().getInventory();
        ItemStack vile = new vile().createItem();

        Integer xpLevelGain = new Random().nextInt(2) + 1;
        String levelWord = (xpLevelGain.equals(1)) ? "level" : "levels";

        this.player.sendMessage(blue + "" + bold + "NIGHT QUEST! "
                + gray + "Done! " + aqua + "+" + xpLevelGain + " XP " + levelWord + "!"
                + darkPurple + " +1 " + vile.getItemMeta().getDisplayName());

        // inv full
        if (playerInventory.firstEmpty() == -1) {
            this.player.getWorld().dropItem(this.player.getLocation(), vile);
            return;
        }

        playerInventory.addItem(vile);
    }
}

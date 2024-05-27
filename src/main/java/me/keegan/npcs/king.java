package me.keegan.npcs;

import me.keegan.items.hats.kings_helmet;
import me.keegan.items.lame.mini_cake;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.npcUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.wordUtil.integerToWord;

public class king extends npcUtil {
    private final Integer neededMiniCakes = 4;
    private final Integer neededExperienceLevels = 250;

    private final ItemStack miniCake = new mini_cake().createItem();
    private final ItemStack kingsHelmet = new kings_helmet().createItem();

    private final String inventoryName = "The King";

    @Override
    public String getNPCName() {
        return "Kings Quest";
    }

    @Override
    public void npcTriggered(Player player, NPC npc) {
        Inventory inventory = ThePitRedux.getPlugin().getServer().createInventory(player, 27, inventoryName);
        inventory.setItem(13, this.createKingMissionItem(player));

        player.openInventory(inventory);
    }

    @EventHandler
    public void inventoryClicked(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(inventoryName)) { return; }
        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();

        // didn't click the helmet or have not met requirements return
        if (e.getSlot() != 13 || !this.playerHasRequirements(player)) { return; }

        this.removeMiniCakes(player);
        player.setLevel(player.getLevel() - neededExperienceLevels);
        player.getInventory().addItem(kingsHelmet.clone());

        player.updateInventory();
        player.closeInventory();
    }

    private void removeMiniCakes(Player player) {
        int count = neededMiniCakes;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || !itemStack.isSimilar(miniCake)) { continue; }
            if (count <= 0) { break; }

            count -= 1;
            itemStack.setAmount(itemStack.getAmount() - 1);
        }
    }

    private boolean hasRequiredMiniCakes(Player player) {
        int count = 0;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || !itemStack.isSimilar(miniCake)) { continue; }

            count += itemStack.getAmount();
        }

        return count >= neededMiniCakes;
    }

    private boolean playerHasRequirements(Player player) {
        return player.getLevel() >= neededExperienceLevels && this.hasRequiredMiniCakes(player);
    }

    private ItemStack createKingMissionItem(Player player) {
        ItemStack itemStack = new ItemStack(kingsHelmet.getType());
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(yellow + "The King's Quest");

        List<String> lore = new ArrayList<>();
        lore.add(darkGray + "Special Mission");
        lore.add("");
        lore.add(gray + "The Task:");
        lore.add(gray + " Bring " + integerToWord(neededMiniCakes) + " " + miniCake.getItemMeta().getDisplayName() + "s");
        lore.add(gray + " Level: " + aqua + Math.min(neededExperienceLevels, player.getLevel()) + "/" + neededExperienceLevels);
        lore.add("");
        lore.add(gray + "The Rewards:");
        lore.add(gold + " 1x " + kingsHelmet.getItemMeta().getDisplayName());
        lore.add("");

        if (this.playerHasRequirements(player)) {
            lore.add(yellow + "Click to complete!");
        }else{
            lore.add(red + "This quest is incomplete!");
            lore.add(red + "Come back when stuff's done!");
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}

package me.keegan.npcs;

import me.keegan.items.pants.aqua_pants;
import me.keegan.items.vile.vile;
import me.keegan.items.vile.vile_block;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.npcUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.itemStackUtil.isSimilar;

public class fisherman extends npcUtil {
    private final String inventoryName = "Fisherman - Sell";

    // integer is xp amount
    private final HashMap<Material, Integer> materialWorthIndex = new HashMap<Material, Integer>(){{
       put(Material.COD, 1);
       put(Material.SALMON, 1);
       put(Material.FISHING_ROD, 3);
       put(Material.PUFFERFISH, 3);
       put(Material.TROPICAL_FISH, 5);
       put(Material.NAME_TAG, 10);
       put(Material.NAUTILUS_SHELL, 10);
       put(Material.SADDLE, 10);
       put(Material.TRIPWIRE_HOOK, 10);
       put(Material.LILY_PAD, 5);
       put(Material.GLASS_BOTTLE, 2);
    }};

    // integer is xp amount
    private final HashMap<ItemStack, Integer> specialWorthIndex = new HashMap<ItemStack, Integer>(){{
        put(new aqua_pants().createItem(), 100);
        put(new vile().createItem(), 300);
        put(new vile_block().createItem(), 2700);
    }};

    private int getSpecialItemstack(ItemStack itemStack) {
        for (Map.Entry<ItemStack, Integer> specialEntrySet : specialWorthIndex.entrySet()) {
            if (!isSimilar(specialEntrySet.getKey(), itemStack)) { continue; }

            return specialEntrySet.getValue();
        }

        return 0;
    }

    private Integer getItemStackWorth(ItemStack itemStack) {
        return (itemStack != null)
            ? Math.max(getSpecialItemstack(itemStack), materialWorthIndex.getOrDefault(itemStack.getType(), 0)) * itemStack.getAmount()
            : 0;
    }

    @Override
    public String getNPCName() {
        return "FISHERMAN";
    }

    @Override
    public void npcTriggered(Player player, NPC npc) {
        player.openInventory(ThePitRedux.getPlugin().getServer().createInventory(player, 27, inventoryName));
    }

    @EventHandler
    public void inventoryClicked(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(inventoryName)) { return; }
        e.setCancelled(true);

        ItemStack itemStack = e.getCurrentItem();
        if (itemStack == null
                || itemStack.getType() == Material.AIR
                || getItemStackWorth(itemStack) == 0) { return; }

        itemStack = itemStack.clone();

        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();
        Inventory clickedInventory = e.getClickedInventory();

        // clicked in sell inventory
        if (inventory.equals(clickedInventory)) {
            if (player.getInventory().firstEmpty() == -1) { return; }

            e.getCurrentItem().setAmount(0);
            player.getInventory().addItem(itemStack);

            player.updateInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 0.85f);
        }else{ // clicked in players inventory
            e.getCurrentItem().setAmount(0);
            inventory.addItem(itemStack);

            player.updateInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1.8f);
        }
    }

    @EventHandler
    public void inventoryClosed(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals(inventoryName)) { return; }
        Player player = (Player) e.getPlayer();

        AtomicInteger totalXPWorth = new AtomicInteger();

        Arrays.stream(e.getInventory().getContents()).forEach(itemStack -> totalXPWorth.set(totalXPWorth.get() + getItemStackWorth(itemStack)));
        player.giveExp(totalXPWorth.get());

        if (totalXPWorth.get() == 0) { return; }

        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1.2f);
        player.sendMessage(lightPurple.toString() + bold + "SOLD!" + reset + gray + " for " + totalXPWorth.get() + " XP!");
    }
}

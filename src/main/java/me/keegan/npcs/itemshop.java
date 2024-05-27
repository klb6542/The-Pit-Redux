package me.keegan.npcs;

import me.keegan.classes.MultiMap;
import me.keegan.items.lame.pants_bundle;
import me.keegan.items.potions.jump_boost;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.npcUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static me.keegan.utils.formatUtil.*;


public class itemshop extends npcUtil {
    // 1st integer = xp cost in levels
    // 2nd double = slot index
    private final MultiMap<ItemStack, Integer, Double> shop = new MultiMap<ItemStack, Integer, Double>(Integer.class, Double.class){{
        put(new jump_boost().createItem(), 5, 11.0);
        put(new pants_bundle().createItem(), 20, 12.0);
    }};

    private final String inventoryName = "Non-permanent items";

    @Override
    public String getNPCName() {
        return "Itemshop";
    }

    @Override
    public void npcTriggered(Player player, NPC npc) {
        Inventory inventory = ThePitRedux.getPlugin().getServer().createInventory(player, 54, inventoryName);

        shop.getKeySet().forEach(itemStack -> {{
            ItemStack itemStackClone = itemStack.clone();

            setItemStackLore(itemStackClone);
            inventory.setItem(shop.getBonus(itemStack).intValue(), itemStackClone);
        }});

        player.openInventory(inventory);
    }

    @Nullable
    private ItemStack getItemStackFromSlot(int slot) {
        AtomicReference<ItemStack> itemStack = new AtomicReference<>(null);

        shop.getKeySet().forEach(shopItemStack -> {{
            if (shop.getBonus(shopItemStack).intValue() == slot) {
                itemStack.set(shopItemStack);
            }
        }});

        return itemStack.get();
    }

    private void setItemStackLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(yellow + ChatColor.stripColor(itemMeta.getDisplayName()));

        List<String> lore = itemMeta.getLore();
        lore.add("");
        lore.add(gray.toString() + italic + "Lost on death");
        lore.add(gray + "Cost: " + aqua + shop.getValue(itemStack) + " XP Levels");
        lore.add(yellow + "Click to purchase!");

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    @EventHandler
    public void inventoryClicked(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(inventoryName)) { return; }
        e.setCancelled(true);

        if (!e.getInventory().equals(e.getClickedInventory())) { return; }
        ItemStack itemStack = getItemStackFromSlot(e.getSlot());
        Player player = (Player) e.getWhoClicked();

        if (player.getInventory().firstEmpty() == -1 || itemStack == null) { return; }
        if (player.getLevel() < shop.getValue(itemStack)) { player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f); return; }

        player.getInventory().addItem(itemStack);
        player.giveExpLevels(-shop.getValue(itemStack));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        player.sendMessage(green.toString() + bold + "PURCHASED! " + gray + "1x " + itemStack.getItemMeta().getDisplayName());
    }
}
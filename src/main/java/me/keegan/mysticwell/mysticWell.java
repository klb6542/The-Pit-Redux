package me.keegan.mysticwell;

import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.MessageFormat;
import java.util.*;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class mysticWell extends itemUtil {
    // inventories represent all the mystic well inventories
    private static final HashMap<UUID, Inventory> inventories = new HashMap<>();
    private static final HashMap<UUID, animation> animations = new HashMap<>();

    @Override
    public String getNamespaceName() {
        return "mystic_well";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.ENCHANTING_TABLE);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(lightPurple + "Mystic Well");

        String description = MessageFormat.format("{0}Find a {1}Mystic Bow{0}, {2}Mystic/n" +
                "{2}Sword{0} or {3}P{4}a{5}n{6}t{7}s{0} from/n" +
                "{0}killing mobs./n" +
                "/n" +
                "{0}Enchant these items in the well/n" +
                "{0}for tons of buffs.",
                gray, aqua, yellow, red, gold, yellow, green, blue);

        itemMeta.setLore(Arrays.asList(description.split("/n")));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {
        NamespacedKey key = new NamespacedKey(
                ThePitRedux.getPlugin(),
                this.getNamespaceName());

        ShapedRecipe recipe = new ShapedRecipe(key, this.createItem());

        // top - middle - bottom
        recipe.shape("ddd", "BOB", "ODO");

        recipe.setIngredient('O', Material.OBSIDIAN);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('d', Material.DIAMOND);
        recipe.setIngredient('B', Material.BOOK);

        ThePitRedux.getPlugin().getServer().addRecipe(recipe);
    }

    // events

    @EventHandler
    public void itemPickedUp(EntityPickupItemEvent e) {
        ItemStack itemStack = e.getItem().getItemStack();
        if (itemStack.getItemMeta() == null) { return; }

        if (!(itemStack.getItemMeta().getDisplayName()
           .equals(this.createItem().getItemMeta().getDisplayName())))
           { return; }

        itemStack.setItemMeta(this.createItem().getItemMeta());
    }

    @EventHandler
    public void inventoryOpened(InventoryOpenEvent e) {
        InventoryView inventoryView = e.getView();

        if (!inventoryView.getTitle()
            .equals(this.createItem().getItemMeta().getDisplayName()))
            { return; }

        e.setCancelled(true);

        Player player = (Player) e.getPlayer();
        UUID uuid = player.getUniqueId();

        player.setCanPickupItems(false);

        // get cached inventory or create a new one
        Inventory inventory = inventories.getOrDefault(uuid,
                ThePitRedux.getPlugin().getServer().createInventory(
                    player,
                    45,
                    ChatColor.stripColor(inventoryView.getTitle())));

        // create new inventory contents if there is no cache
        if (!inventories.containsKey(uuid)
                || !animations.containsKey(uuid)) {

            inventory.setItem(25, this.createItem());

            inventory.setItem(10, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            inventory.setItem(11, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            inventory.setItem(12, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            inventory.setItem(19, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            inventory.setItem(21, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            inventory.setItem(28, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            inventory.setItem(29, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            inventory.setItem(30, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

            List<ItemStack> glassPanes = new ArrayList<>();

            glassPanes.addAll(Arrays.asList(
                    inventory.getItem(10),
                    inventory.getItem(11),
                    inventory.getItem(12),
                    inventory.getItem(21),
                    inventory.getItem(30),
                    inventory.getItem(29),
                    inventory.getItem(28),
                    inventory.getItem(19)));

            animation animation = new animation(inventory, glassPanes);
            animation.idleState();

            animations.put(uuid, animation);
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void inventoryClosed(InventoryCloseEvent e) {
        InventoryView inventoryView = e.getView();
        Inventory inventory = e.getInventory();

        Player player = (Player) e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!inventoryView.getTitle()
            .equals(ChatColor.stripColor(this.createItem().getItemMeta().getDisplayName())))
            { return; }

        player.setCanPickupItems(true);

        if (animations.get(uuid).isEnchanting) {
            inventory.setItem(20, animations.get(uuid).itemStack);

            animations.get(uuid).cancelRunnableTasks();
            animations.remove(uuid);
        } else if (animations.get(uuid).isEnchantingComplete) { // use else if to avoid error if animation is removed from hashmap
            animations.get(uuid).idleState();
        }

        if (inventory.getItem(20) == null) {
            inventories.put(uuid, inventory);
            return;
        }

        // full inventory
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), inventory.getItem(20));
        }else{
            player.getInventory().addItem(inventory.getItem(20));
        }

        inventory.setItem(20, new ItemStack(Material.AIR));

        // cache only when mystic is removed from slot so there are no duplicates
        inventories.put(uuid, inventory);
    }

    @EventHandler
    public void inventoryClicked(InventoryClickEvent e) {
        InventoryView inventoryView = e.getView();

        Inventory inventory = e.getInventory();
        Inventory inventoryClicked = e.getClickedInventory();

        ItemStack currentItemStack = e.getCurrentItem();

        int inventorySlot = e.getSlot();

        // clicked out of inventory
        if (inventorySlot < 0 || currentItemStack == null) { return; }

        // check if they opened the mystic well
        if (!inventoryView.getTitle()
            .equals(ChatColor.stripColor(this.createItem().getItemMeta().getDisplayName())))
            { return; }

        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();
        e.setCancelled(true);

        // return if animation is playing the enchanting state
        if (animations.get(uuid).isEnchanting) { return; }

        // place mystic in mystic well slot
        if (!inventory.equals(inventoryClicked)) {
            if (inventory.getItem(20) != null
                    || !mysticUtil.getInstance().isMystic(currentItemStack)) { return; }

            e.setCancelled(true);

            inventory.setItem(20, currentItemStack);
            currentItemStack.setAmount(0);

            player.updateInventory();
            return;
        }

        // clicked itemstack in mystic well slot
        if (inventorySlot == 20
            && inventory.getItem(inventorySlot) != null
            && player.getInventory().firstEmpty() != -1) {

            if (animations.get(uuid).isEnchantingComplete) {
                animations.get(uuid).idleState();
            }

            inventory.setItem(inventorySlot, null);

            player.getInventory().addItem(currentItemStack);
            player.updateInventory();
        }

        // clicked mystic well to enchant
        if (inventorySlot == 25 && inventory.getItem(20) != null) {
            animations.get(uuid).enchantingState();
        }
    }
}

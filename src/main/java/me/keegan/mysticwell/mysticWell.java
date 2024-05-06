package me.keegan.mysticwell;

import me.keegan.classes.Tier;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
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
import static me.keegan.utils.romanUtil.integerToRoman;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class mysticWell extends itemUtil {
    static final Tier<Integer> xpLevelsEnchantCostPerTier = new Tier<>(1, 2, 3);

    // inventories represent all the mystic well inventories
    static final HashMap<UUID, Inventory> inventories = new HashMap<>();
    static final HashMap<UUID, animation> animations = new HashMap<>();

    private final boolean requirePantsToTierThree = false;

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

    // don't use this method for mystic well display because it checks slot 20
    // and mystic well display method uses animation.itemstack
    private static boolean canEnchantItemStack(Player player, Inventory inventory) {
        return inventory.getItem(20) != null
                && player.getLevel() >=
                xpLevelsEnchantCostPerTier.getOrDefault(mysticUtil.getInstance().getTier(inventory.getItem(20)), 9999999);
    }

    static void playHighPitchSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1.3f);
    }

    static void playLowPitchSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 0.6f);
    }

    static void playCustomPitchSound(Player player, float pitch) {
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, pitch);
    }

    // toggles and updates mystic well lore
    static void toggleEnchantCostDisplay(Player player, Inventory inventory, Boolean visibility) {
        // mystic well itemstack
        ItemStack itemStack = inventory.getItem(25);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!visibility) {
            itemMeta.setLore(new mysticWell().createItem().getItemMeta().getLore());
            itemStack.setItemMeta(itemMeta);
            return;
        }

        animation animation = animations.get(player.getUniqueId());
        Integer mysticTier = mysticUtil.getInstance().getTier(animation.itemStack);

        ChatColor costChatColor
                = (player.getLevel() >= xpLevelsEnchantCostPerTier.getOrDefault(mysticUtil.getInstance().getTier(animation.itemStack), 9999999))
                ? aqua
                : red;
        ChatColor chatColor = mysticUtil.getInstance().getItemStackTierColor(
                new ItemStack(Material.GOLDEN_SWORD), // used to get the correct chat color
                mysticTier + 1
        );
        String pantsCost
                = (mysticUtil.getInstance().getTier(animation.itemStack) == 2)
                ? lightPurple + "Dyed Pants " + gray + "+ "
                : "";

        List<String> lore = new ArrayList<>();
        lore.add(gray + "Upgrade: " + chatColor + "Tier " + integerToRoman((mysticTier + 1), false));
        lore.add(gray + "Cost: " + pantsCost + costChatColor + xpLevelsEnchantCostPerTier.getOrDefault(mysticTier, 9999999) + " XP Levels");
        lore.add("");
        lore.add(yellow + "Click to upgrade!");

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    static void displayPantsSlot(Inventory inventory) {
        ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(lightPurple + "Pants Slot");

        List<String> lore = new ArrayList<>();
        lore.add(gray + "This Tier III upgrade");
        lore.add(gray + "requires " + lightPurple + "Dyed Pants");
        lore.add("");
        lore.add(yellow + "Click pants in your inventory!");

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        inventory.setItem(23, itemStack);
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

            animation animation = new animation(inventory, glassPanes, player);
            animation.idleState();

            animations.put(uuid, animation);
        }

        animations.get(uuid).resetIdleMaxState();
        playHighPitchSound(player);

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
        animation animation = animations.get(uuid);

        if (animation.isEnchanting) {
            inventory.setItem(20, animation.itemStack);

            animation.cancelRunnableTasks();
            animations.remove(uuid);
        } else if (animation.isEnchantingComplete) { // use else if to avoid error if animation is removed from hashmap
            animation.idleState();
        }

        if (inventory.getItem(20) == null && inventory.getItem(23) == null) {
            inventories.put(uuid, inventory);
            return;
        }

        ItemStack[] itemStacks = new ItemStack[]{inventory.getItem(20), inventory.getItem(23)};

        for (ItemStack itemStack : itemStacks) {
            if (itemStack == null || !mysticUtil.getInstance().isMystic(itemStack)) { continue; }

            // full inventory
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItem(player.getLocation(), itemStack);
            }else{
                player.getInventory().addItem(itemStack);
            }
        }

        playLowPitchSound(player);
        inventory.setItem(20, null);
        inventory.setItem(23, null);

        // set glass pane back to default
        animation.glassPaneMaterial = mysticUtil.getInstance().getItemStackTierGlassPane(
                animation.itemStack,
                -1
        );

        toggleEnchantCostDisplay(player, inventory, false);

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

        animation animation = animations.get(uuid);

        // return if animation is playing the enchanting state
        if (animation.isEnchanting) { return; }

        // place mystic in mystic well slot
        if (!inventory.equals(inventoryClicked)
                && mysticUtil.getInstance().isMystic(currentItemStack)
                && animation.isAllowedToEnchant) {

            // 1st mystic in mystic well
            if (inventory.getItem(20) == null) {
                animation.itemStack = currentItemStack.clone();
                animation.glassPaneMaterial = mysticUtil.getInstance().getItemStackTierGlassPane(
                        animation.itemStack,
                        mysticUtil.getInstance().getTier(animation.itemStack)
                );

                toggleEnchantCostDisplay(player, inventory, true);
                playHighPitchSound(player);

                if (mysticUtil.getInstance().getTier(currentItemStack) == 2) {
                    displayPantsSlot(inventory);
                }

                inventory.setItem(20, currentItemStack);
                currentItemStack.setAmount(0);

                player.updateInventory();
                return;
            }else{
                ItemStack itemStack = inventory.getItem(20);

                if (mysticUtil.getInstance().getTier(itemStack) != 2
                        || currentItemStack.getType() != Material.LEATHER_LEGGINGS
                        || currentItemStack.getItemMeta() == null
                        || !currentItemStack.getItemMeta().getDisplayName().contains("Dyed")) { return; }
                inventory.setItem(23, currentItemStack);
                currentItemStack.setAmount(0);

                playHighPitchSound(player);
                player.updateInventory();
                return;
            }
        }

        // clicked itemstack in pants slot
        if (inventorySlot == 23
                && inventory.getItem(inventorySlot) != null
                && player.getInventory().firstEmpty() != -1
                && mysticUtil.getInstance().isMystic(currentItemStack)) {

            playLowPitchSound(player);

            player.getInventory().addItem(currentItemStack);
            inventory.setItem(inventorySlot, null);

            displayPantsSlot(inventory);
            player.updateInventory();
        }

        // clicked itemstack in mystic well slot
        if (inventorySlot == 20
            && inventory.getItem(inventorySlot) != null
            && player.getInventory().firstEmpty() != -1) {

            if (animation.isEnchantingComplete || !animation.isAllowedToEnchant) {
                animation.resetIdleMaxState();

                // do not use guard-clause
                if (animation.isEnchantingComplete) {
                    animation.idleState();
                }
            }

            animation.itemStack = new ItemStack(Material.AIR);
            animation.glassPaneMaterial = mysticUtil.getInstance().getItemStackTierGlassPane(
                    animation.itemStack,
                    mysticUtil.getInstance().getTier(animation.itemStack)
            );

            inventory.setItem(inventorySlot, null);

            if (inventory.getItem(23) != null
                    && mysticUtil.getInstance().isMystic(inventory.getItem(23))) {
                player.getInventory().addItem(inventory.getItem(23));
                inventory.setItem(23, null);
            }else {
                inventory.setItem(23, null);
            }

            toggleEnchantCostDisplay(player, inventory, false);
            playLowPitchSound(player);

            player.getInventory().addItem(currentItemStack);
            player.updateInventory();
        }

        // clicked mystic well to enchant
        if (inventorySlot == 25
                && inventory.getItem(20) != null
                && animation.isAllowedToEnchant
                && canEnchantItemStack(player, inventory)) {

            // if item is tier 2 and the pants slot is empty return
            if (mysticUtil.getInstance().getTier(inventory.getItem(20)) == 2
                    && inventory.getItem(23) != null
                    && !mysticUtil.getInstance().isMystic(inventory.getItem(23))
                    && requirePantsToTierThree) {
                return;
            }

            // mystic well itemstack
            ItemStack itemStack = inventory.getItem(25);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setLore(new ArrayList<>());
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(23, null);
            player.updateInventory();

            animation.enchantingState();
        }
    }
}
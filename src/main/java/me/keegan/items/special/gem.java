package me.keegan.items.special;

import me.keegan.builders.mystic;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import me.keegan.utils.propertiesUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class gem extends itemUtil {
    // mystics that are currently being upgraded in the totally legit gem inventory
    // itemstack is immutable
    private static final HashMap<UUID, ItemStack> mystics = new HashMap<>();

    // mystics in the selector inventory, itemstack is mystic while integer is inventory slot of player
    private static final HashMap<UUID, HashMap<ItemStack, Integer>> playerMystics = new HashMap<>();

    private final String inventoryName = "Totally Legit Selector";
    private final String gemName = green + "Totally Legit Gem";
    private final String gemCost = ChatColor.getByChar(gemName.substring(1, 2)) + "1 " + gemName;

    private final Boolean gemRareEnchants = true;
    private final Boolean mysticMustBeTier3 = true;

    public static final String gemIndicator = green + "♦";

    @Override
    public String getNamespaceName() {
        return "gem";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.EMERALD);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(gemName);
        List<String> lore = new ArrayList<>();

        lore.add(mystic.defaultLore.get(0));
        lore.add("");
        lore.add(gray + "Adds " + lightPurple + "1 tier" + gray + " to a mystic enchant");
        lore.add(darkGray + "Once per item!");
        lore.add("");
        lore.add(yellow + "Hold and right-click to use!");

        propertiesUtil.setProperty(propertiesUtil.notCraftable, itemMeta);

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {

    }

    private String getFooter(ItemStack itemStack) {
        if (mysticUtil.getInstance().isGemmed(itemStack)) {
            return red + "Item has already been upgraded!";
        }

        if (this.mysticMustBeTier3 && mysticUtil.getInstance().getTier(itemStack) < 3) {
            return red + "Item needs to be Tier III!";
        }

        return yellow + "Click to upgrade!";
    }

    private void removeLoreFooter(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null
                || itemMeta.getLore() == null
                || itemMeta.getLore().isEmpty()) { return; }

        List<String> lore = itemMeta.getLore();

        // i starts at footer
        for (int i = lore.size() - 2; i < lore.size();) {
            lore.remove(i);
         }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    private ItemStack createEnchantMaterial(ItemStack mystic, enchantUtil enchant, int slot) {
        ItemStack itemStack = new ItemStack(mystic.getType());
        ItemMeta itemMeta = itemStack.getItemMeta();

        int tokens = mysticUtil.getInstance().getEnchantTokens(mystic, enchant);

        String cost = gemCost;

        if (mysticUtil.getInstance().getEnchantLevel(mystic, enchant) >= enchant.getMaxLevel()) {
            cost = red + "Maxed out!";
        }

        if (enchant.isRareEnchant() && !this.gemRareEnchants) {
            cost = red + "Can't upgrade rare!";
        }

        itemMeta.setDisplayName(yellow + "Upgrade Slot " + slot);
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(gray + "Upgrade: " + blue + enchant.getName());
        lore.add(gray + "Tier: " + yellow + integerToRoman(tokens, false));
        lore.add("");
        lore.add(gray + "Cost: " + cost);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private Integer getUpgradeIndex(List<String> lore) {
        List<Integer> upgradeIndexes = lore
                .stream()
                .filter(string -> string.contains(gray + "Upgrade"))
                .map(lore::indexOf)
                .collect(Collectors.toList());

        return (!upgradeIndexes.isEmpty())
                ? upgradeIndexes.get(0)
                : -1;
    }

    @EventHandler
    public void playerInteracted(PlayerInteractEvent e) {
        if ((e.getAction() != Action.RIGHT_CLICK_AIR
                && e.getAction() != Action.RIGHT_CLICK_BLOCK)
                || e.getItem() == null
                || !e.getItem().isSimilar(this.createItem())) { return; }
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        // create gem selector inventory
        Inventory inventory = ThePitRedux.getPlugin().getServer().createInventory(
                player,
                36,
                inventoryName);

        List<ItemStack> mystics = mysticUtil.getInstance().getPlayerMystics(player, true, true);

        for (ItemStack itemStack : mystics) {
            String loreFooter = this.getFooter(itemStack);

            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = itemMeta.getLore();
            lore.add("");
            lore.add(loreFooter);

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }

        inventory.addItem(mystics.toArray(new ItemStack[0]));

        // set default slot to 0, update it in next method
        mystics.forEach(itemStack -> {
            playerMystics.put(uuid, playerMystics.getOrDefault(uuid, new HashMap<>()));
            playerMystics.get(uuid).put(itemStack, 0);
        });

        player.openInventory(inventory);
    }

    // selector inventory
    @EventHandler
    public void inventoryClicked(InventoryClickEvent e) {
        InventoryView inventoryView = e.getView();
        if (!inventoryView.getTitle().equals(inventoryName)) { return; }

        e.setCancelled(true);

        Inventory currentInventory = e.getInventory();
        Inventory clickedInventory = e.getClickedInventory();
        ItemStack itemStack = e.getCurrentItem();

        if (!currentInventory.equals(clickedInventory) || itemStack == null) { return; }

        // TODO
        // add checks for tier 3 and if its already gemmed

        itemStack = itemStack.clone(); // immutable

        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();

        player.closeInventory();

        // create gem inventory
        Inventory inventory = ThePitRedux.getPlugin().getServer().createInventory(
                player,
                36,
                ChatColor.stripColor(gemName));

        List<enchantUtil> enchants = mysticUtil.getInstance().getEnchants(itemStack);

        // math to calculate where the first enchant should go in the inventory slots
        int slotIndex = ((9 - ((enchants.size() * 2) - 1)) / 2) + 9;
        if (slotIndex < 0) { return; }

        for (int i = 0; i < enchants.size(); i++) {
            inventory.setItem(slotIndex, this.createEnchantMaterial(itemStack, enchants.get(i), i + 1));
            slotIndex += 2;
        }

        mystics.put(uuid, e.getCurrentItem());

        // update playerMystics inventory slot
        playerMystics.get(uuid).put(e.getCurrentItem(), e.getSlot());
        player.openInventory(inventory);
    }

    // mystic enchants inventory
    @EventHandler
    public void inventoryClicked2(InventoryClickEvent e) {
        InventoryView inventoryView = e.getView();

        if (!inventoryView.getTitle().equals(
               ChatColor.stripColor(gemName))) { return; }

        e.setCancelled(true);

        Inventory currentInventory = e.getInventory();
        Inventory clickedInventory = e.getClickedInventory();
        ItemStack itemStack = e.getCurrentItem();

        if (!currentInventory.equals(clickedInventory) || itemStack == null) { return; }
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();

        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();
        player.closeInventory();

        if (!lore.get(lore.size() - 1).contains(gemCost)
                || this.getUpgradeIndex(lore) == -1) { return; }

        ItemStack currentMystic = mystics.get(player.getUniqueId()); // immutable
        List<enchantUtil> currentsEnchants = mysticUtil.getInstance().getEnchants(currentMystic);
        PlayerInventory playerInventory = player.getInventory();

        for (enchantUtil enchant : currentsEnchants) {
            if (!lore.get(this.getUpgradeIndex(lore)).contains(blue + enchant.getName())) { continue; }
            playerInventory.getItem(playerMystics.get(uuid).get(currentMystic)).setAmount(0);

            mysticUtil.getInstance().addEnchantLevel(currentMystic, enchant, 1);
            mysticUtil.getInstance().gem(currentMystic);

            this.removeLoreFooter(currentMystic);
            playerInventory.addItem(currentMystic);
            break;
        }
    }
}
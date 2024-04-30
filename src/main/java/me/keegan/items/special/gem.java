package me.keegan.items.special;

import me.keegan.builders.mystic;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import me.keegan.utils.propertiesUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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

    private final String inventoryName = "Totally Legit Selector";
    private final String gemName = green + "Totally Legit Gem";
    private final String gemCost = ChatColor.getByChar(gemName.substring(1, 2)) + "1 " + gemName;
    private final String gemUpgradeFooter = yellow + "Click to upgrade!";

    private final Boolean gemRareEnchants = false;
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
        return (this.mysticMustBeTier3 && mysticUtil.getInstance().getTier(itemStack) != 3)
                ? red + "Item needs to be Tier III!"
                : this.gemUpgradeFooter;
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

    private void removeOldMystic(Player player) {
        ItemStack currentMystic = mystics.get(player.getUniqueId()).clone(); // immutable
        PlayerInventory playerInventory = player.getInventory();

        this.removeLoreFooter(currentMystic);

        for (ItemStack itemStack : playerInventory) {
            if (itemStack == null || !itemStack.isSimilar(currentMystic)) { continue; }

            itemStack.setAmount(0);
            break;
        }
    }

    private void removeGem(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack mainHandItemStack = playerInventory.getItemInMainHand();
        ItemStack offHandItemStack = playerInventory.getItemInOffHand();

        if (mainHandItemStack.isSimilar(this.createItem())) {
            mainHandItemStack.setAmount(mainHandItemStack.getAmount() - 1);
            return;
        }

        if (offHandItemStack.isSimilar(this.createItem())) {
            offHandItemStack.setAmount(offHandItemStack.getAmount() - 1);
        }
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

        // filter gemmed mystics
        List<ItemStack> mystics = mysticUtil.getInstance().getPlayerMystics(player, true, true)
                .stream()
                .filter(itemStack -> !mysticUtil.getInstance().isGemmed(itemStack))
                .collect(Collectors.toList());

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

        if (!currentInventory.equals(clickedInventory)
                || itemStack == null
                || (this.mysticMustBeTier3 && mysticUtil.getInstance().getTier(itemStack) != 3)) { return; }
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

        ItemStack currentMystic = mystics.get(uuid); // immutable
        List<enchantUtil> currentsEnchants = mysticUtil.getInstance().getEnchants(currentMystic);
        PlayerInventory playerInventory = player.getInventory();

        for (enchantUtil enchant : currentsEnchants) {
            // continue when enchant name is not found in upgrade string or when player has item equipped
            if (!lore.get(this.getUpgradeIndex(lore)).contains(blue + enchant.getName())) { continue; }

            // removes old, ungemmed mystic
            this.removeOldMystic(player);

            // remove one gem from player's inventory
            this.removeGem(player);

            mysticUtil.getInstance().addEnchantLevel(currentMystic, enchant, 1);
            mysticUtil.getInstance().gem(currentMystic);

            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.1f, 9f);

            this.removeLoreFooter(currentMystic);
            playerInventory.addItem(currentMystic);
            break;
        }
    }
}
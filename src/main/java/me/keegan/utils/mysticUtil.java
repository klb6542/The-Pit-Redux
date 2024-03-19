package me.keegan.utils;

import me.keegan.enchantments.Guts;
import me.keegan.pitredux.ThePitRedux;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.*;
import static org.bukkit.Material.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class mysticUtil implements CommandExecutor {
    private static final List<enchantUtil> enchants = new ArrayList<>();
    private static final List<enchantUtil> swordEnchants = new ArrayList<>();
    private static final List<enchantUtil> bowEnchants = new ArrayList<>();
    private static final List<enchantUtil> pantsEnchants = new ArrayList<>();

    public static mysticUtil getInstance() {
        return new mysticUtil();
    }

    private @Nullable List<String> getItemLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) { return null; }

        return (itemMeta.hasLore()) ? itemMeta.getLore() : null;
    }

    private Integer getEnchantIndex(ItemStack itemStack, enchantUtil enchant, List<String> lore) {
        // returns the index where the enchantment is
        for (int i = 0; i < lore.size(); i++) {
            if (!lore.get(i).contains(blue + enchant.getName())) { continue; }

            return i;
        }

        return -1;
    }

    private Boolean containsEnchant(List<String> lore, Integer loreIndex, Iterator iterator) {
        // used only for getEnchantCount method & getTokens method
        while (iterator.hasNext()) {
            enchantUtil enchant = (enchantUtil) iterator.next();
            if (!lore.get(loreIndex).contains(blue + enchant.getName())) { continue; }

            return true;
        }

        return false;
    }

    public void registerEnchant(enchantUtil enchant) {
        // register listener
        ThePitRedux.getPlugin().getServer().getPluginManager().registerEvents(enchant, ThePitRedux.getPlugin());

        // register enchant to list
        enchants.add(enchant);

        for (int i = 0; i < enchant.getEnchantMaterial().length; i++) {
            // sort the enchants to make it easier for getEnchantCount method
            // don't break because enchants can have multiple materials
            switch (enchant.getEnchantMaterial()[i]) {
                case GOLDEN_SWORD:
                    swordEnchants.add(enchant);
                case BOW:
                    bowEnchants.add(enchant);
                case LEATHER_LEGGINGS:
                    pantsEnchants.add(enchant);
            }

        }
    }

    public List<enchantUtil> getEnchantList() {
        return enchants;
    }

    public Integer getEnchantCount(ItemStack itemStack) {
        List<String> lore = this.getItemLore(itemStack);
        int count = 0;

        if (lore == null) { return count; }

        for (int i = 0; i < lore.size(); i++) {

            switch (itemStack.getType()) {
                case GOLDEN_SWORD:
                    count += (this.containsEnchant(lore, i, swordEnchants.iterator())) ? 1 : 0;
                    break;
                case BOW:
                    count += (this.containsEnchant(lore, i, bowEnchants.iterator())) ? 1 : 0;
                    break;
                case LEATHER_LEGGINGS:
                    count += (this.containsEnchant(lore, i, pantsEnchants.iterator())) ? 1 : 0;
                    break;
            }

        }

        return count;
    }

    private Integer retrieveEnchantTokens(List<String> lore, Integer enchantIndex) {
        // used only for getTokens method
        String[] splitLore = lore.get(enchantIndex).split(" ");

        return romanToInteger(splitLore[splitLore.length - 1]);
    }

    public Integer getEnchantTokens(ItemStack itemStack) {
        List<String> lore = this.getItemLore(itemStack);
        int tokens = 0;
        if (lore == null) { return tokens; }

        for (int i = 0; i < lore.size(); i++) {

            switch (itemStack.getType()) {
                case GOLDEN_SWORD:
                    if (!this.containsEnchant(lore, i, swordEnchants.iterator())) { break; }
                    tokens += this.retrieveEnchantTokens(lore, i);
                    break;
                case BOW:
                    if (!this.containsEnchant(lore, i, bowEnchants.iterator())) { break; }
                    tokens += this.retrieveEnchantTokens(lore, i);
                    break;
                case LEATHER_LEGGINGS:
                    if (!this.containsEnchant(lore, i, pantsEnchants.iterator())) { break; }
                    tokens += this.retrieveEnchantTokens(lore, i);
                    break;
            }
        }

        return tokens;
    }

    public Boolean hasEnchant(ItemStack itemStack, enchantUtil enchant) {
        List<String> lore = this.getItemLore(itemStack);

        return lore != null && lore.stream().anyMatch(s -> s.contains(blue + enchant.getName()));
    }

    public void addEnchant(ItemStack itemStack, enchantUtil enchant, Integer enchantLevel) {
        if (this.hasEnchant(itemStack, enchant)) { ThePitRedux.getPlugin().getLogger().info("This enchant is already on the item!"); return; }

        enchantLevel = Math.max(1, Math.min(3, enchantLevel));; // fix (basically Math.clamp(1, 3, enchantLevel))
        List<String> lore = (this.getItemLore(itemStack) != null) ? this.getItemLore(itemStack) : new ArrayList<>();
        ItemMeta itemMeta = itemStack.getItemMeta();

        String name = (enchant.isRareEnchant())
                ? MessageFormat.format("{0}RARE! {1}{2} {3}", lightPurple, blue, enchant.getName(), integerToRoman(enchantLevel, true))
                : MessageFormat.format("{0}{1} {2}", blue, enchant.getName(), integerToRoman(enchantLevel, true));
        String description = enchant.getEnchantDescription()[enchantLevel - 1];

        if (!lore.isEmpty()) {
            lore.add("");
        }

        lore.add(name);
        lore.add(description);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public void removeEnchant(ItemStack itemStack, enchantUtil enchant) {
        if (!this.hasEnchant(itemStack, enchant)) { return; }

        List<String> lore = this.getItemLore(itemStack); // already check for item-meta and lore with hasEnchant method
        List<String> newLore = new ArrayList<>();
        ItemMeta itemMeta = itemStack.getItemMeta();

        for (int i = 0; i < lore.size(); i++) {
            if (!lore.get(i).contains(enchant.getName())) { // write old enchant lore to new enchant lore
                newLore.add(i, lore.get(i));
            }

            for (int j = i; j < lore.size(); j++) { // reaches enchant to skip over
                if (!lore.get(j).isEmpty()) { i++; }

                break; // stop loop when reaches end of enchantment description
            }
        }

        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
    }

    public Integer getEnchantLevel(ItemStack itemStack, enchantUtil enchant) {
        List<String> lore = this.getItemLore(itemStack);
        if (lore == null) { return 0; }

        Integer enchantIndex = this.getEnchantIndex(itemStack, enchant, lore);
        if (enchantIndex == -1) { return 0; }

        String[] splitLore = lore.get(enchantIndex).split(" ");

        return romanToInteger(splitLore[splitLore.length - 1]);
    }

    public void addLives(ItemStack itemStack, Integer lives) {

    }

    public void addMaxLives(ItemStack itemStack, Integer maxLives) {

    }

    public void removeLives(ItemStack itemStack, Integer lives) {

    }

    public void removeMaxLives(ItemStack itemStack, Integer maxLives) {

    }

    public Integer getLives(ItemStack itemStack) {
        return 0;
    }

    public Integer getMaxLives(ItemStack itemStack) {
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        ItemStack itemStack = new ItemStack(GOLDEN_SWORD);

        this.addEnchant(itemStack, new Guts(), 2);
        this.addEnchant(itemStack, new Guts(), 0);
        this.addEnchant(itemStack, new Guts(), 5);
        ThePitRedux.getPlugin().getLogger().info(String.valueOf(this.getEnchantTokens(itemStack)) + " is the amount of tokens!");

        ThePitRedux.getPlugin().getServer().getPlayer("qsmh").getInventory().addItem(itemStack);
        return true;
    }
}
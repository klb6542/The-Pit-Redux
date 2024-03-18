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
import java.util.List;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class mysticUtil implements CommandExecutor {
    private final List<enchantUtil> enchants = new ArrayList<>();

    public static mysticUtil getInstance() {
        return new mysticUtil();
    }

    private @Nullable List<String> getItemLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) { return null; }

        return (!itemMeta.hasLore()) ? null : itemMeta.getLore();
    }

    public void registerEnchant(enchantUtil enchant) {
        ThePitRedux.getPlugin().getServer().getPluginManager().registerEvents(enchant, ThePitRedux.getPlugin()); // register listener

        this.enchants.add(enchant); // register enchant to list
    }

    public List<enchantUtil> getEnchants() {
        return this.enchants;
    }

    public Boolean hasEnchant(ItemStack itemStack, enchantUtil enchant) {
        List<String> lore = this.getItemLore(itemStack);

        return lore != null && lore.stream().anyMatch(s -> s.contains(enchant.getName()));
    }

    public void addEnchant(ItemStack itemStack, enchantUtil enchant, Integer enchantLevel) {
        if (this.hasEnchant(itemStack, enchant)) { ThePitRedux.getPlugin().getLogger().info("This enchant is already on the sword!"); return; }
        if (itemStack.getItemMeta() == null) { return; }

        enchantLevel = (enchantLevel - 1 < enchant.getMaxLevel()) ? enchantLevel : 1; // fix
        List<String> lore = (this.getItemLore(itemStack) != null) ? this.getItemLore(itemStack) : new ArrayList<>();
        ItemMeta itemMeta = itemStack.getItemMeta();

        String name = (enchant.isRareEnchant())
                ? MessageFormat.format("{0}RARE! {1}{2} {3}", lightPurple, blue, enchant.getName(), integerToRoman(enchantLevel))
                : MessageFormat.format("{0}{1} {2}", blue, enchant.getName(), integerToRoman(enchantLevel));
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

    }

    public Integer getEnchantLevel() {
        return 0;
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
        ThePitRedux.getPlugin().getLogger().info("YOO S");

        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);

        this.addEnchant(itemStack, new Guts(), 3);
        this.addEnchant(itemStack, new Guts(), 2);

        ThePitRedux.getPlugin().getServer().getPlayer("qsmh").getInventory().addItem(itemStack);

        return true;
    }
}

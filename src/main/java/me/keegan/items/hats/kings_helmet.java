package me.keegan.items.hats;

import me.keegan.enchantments.Royalty;
import me.keegan.enums.livesEnums;
import me.keegan.enums.mysticEnums;
import me.keegan.items.lame.mini_cake;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

/*
 * Copyright (c) 2024. Created by klb.
 */


public class kings_helmet extends itemUtil {
    private final Integer protectionLevel = 5;

    @Override
    public String getNamespaceName() {
        return "kings_helmet";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.GOLDEN_HELMET);

        mysticUtil.getInstance().addLives(itemStack, 5, livesEnums.MAX_LIVES);
        mysticUtil.getInstance().addLives(itemStack, 5, livesEnums.LIVES);
        mysticUtil.getInstance().addEnchant(itemStack, new Royalty(), 1, false);

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(new NamespacedKey(ThePitRedux.getPlugin(), "mystic"),
                PersistentDataType.STRING, mysticEnums.HELMET.toString().toLowerCase());

        itemMeta.setDisplayName(gold + "King's Helmet");

        List<String> lore = itemMeta.getLore();
        lore.add("");
        lore.add(gold + "Protection " + integerToRoman(protectionLevel, false));

        // boolean is to add unsafe levels
        itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel, true);

        itemMeta.setUnbreakable(true);

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {

    }
}

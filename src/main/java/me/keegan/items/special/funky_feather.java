package me.keegan.items.special;

import me.keegan.utils.itemUtil;
import me.keegan.utils.propertiesUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.itemStackUtil.isSimilar;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class funky_feather extends itemUtil {
    @Override
    public String getNamespaceName() {
        return "funky_feather";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.FEATHER);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(darkAqua + "Funky Feather");
        List<String> lore = new ArrayList<>();

        lore.add(yellow + "Special Item");
        lore.add(gray + "Protects your inventory but gets");
        lore.add(gray + "consumed on death if in your");
        lore.add(gray + "hotbar.");

        propertiesUtil.setProperty(propertiesUtil.notCraftable, itemMeta);
        propertiesUtil.setProperty(propertiesUtil.unavailableForAnvil, itemMeta);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {

    }

    @Nullable
    private ItemStack getFunkyFeather(PlayerInventory inventory) {
        ItemStack funkyFeather = this.createItem();

        for (int i = 0; i < 8; i++) {
            if (inventory.getItem(i) == null || !isSimilar(inventory.getItem(i), funkyFeather)) { continue; }

            return inventory.getItem(i);
        }

        return null;
    }

    @EventHandler
    public void playerDied(PlayerDeathEvent e) {
        Player player = e.getEntity();
        ItemStack funkyFeather = getFunkyFeather(player.getInventory());
        if (funkyFeather == null) { return; }

        funkyFeather.setAmount(funkyFeather.getAmount() - 1);

        e.setDroppedExp(0);
        e.getDrops().clear();
        e.setKeepInventory(true);
        e.setKeepLevel(true);

        player.updateInventory();
        player.sendMessage(darkAqua + "" + bold + "FUNKY FEATHER! " + gray + "inventory protected!");
    }
}

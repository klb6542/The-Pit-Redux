package me.keegan.handlers;

import me.keegan.utils.mysticUtil;
import me.keegan.utils.propertiesUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

import static me.keegan.utils.itemUtil.dyes;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class mysticHandler implements Listener {
    private boolean containsDye(CraftingInventory craftingInventory) {
        // https://docs.oracle.com/javase/tutorial/essential/concurrency/atomicvars.html
        AtomicBoolean isDye = new AtomicBoolean(false);

        // loop through each item in inventory, if it is a dye then set boolean to true and keep it true
        craftingInventory.forEach(itemStack -> {
            if (itemStack != null) {
                isDye.set(isDye.get() || dyes.contains(itemStack.getType()));
            }
        });

        return isDye.get();
    }

    private boolean containsNotCraftable(CraftingInventory craftingInventory) {
        AtomicBoolean containsNotCraftable = new AtomicBoolean(false);

        craftingInventory.forEach(itemStack ->
                containsNotCraftable.set(
                        containsNotCraftable.get()
                        || propertiesUtil.hasProperty(propertiesUtil.notCraftable, itemStack.getItemMeta())));

        return containsNotCraftable.get();
    }

    @EventHandler
    public void prepareAnvil(PrepareAnvilEvent e) {
        if (mysticUtil.getInstance().isMystic(e.getResult())) { e.setResult(new ItemStack(Material.AIR)); }
    }

    @EventHandler
    public void prepareItemEnchant(PrepareItemEnchantEvent e) {
        e.setCancelled(mysticUtil.getInstance().isMystic(e.getItem()));
    }

    @EventHandler
    public void craftItem(CraftItemEvent e) {
        e.setCancelled((mysticUtil.getInstance().isMystic(e.getCurrentItem()) && containsDye(e.getInventory()))
                || containsNotCraftable(e.getInventory()));
    }

    @EventHandler
    public void furnaceBurn(FurnaceBurnEvent e) {
        e.setCancelled(propertiesUtil.hasProperty(propertiesUtil.notBurnable, e.getFuel().getItemMeta()));
    }

    @EventHandler
    public void blockPlaced(BlockPlaceEvent e) {
        if (e.getItemInHand().getItemMeta() == null) { return; }

        e.setCancelled(propertiesUtil.hasProperty(propertiesUtil.notPlaceable, e.getItemInHand().getItemMeta()));
    }
}

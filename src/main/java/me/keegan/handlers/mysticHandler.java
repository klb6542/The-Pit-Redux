package me.keegan.handlers;

import me.keegan.utils.mysticUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class mysticHandler implements Listener {
    @EventHandler
    public void prepareAnvil(PrepareAnvilEvent e) {
        if (mysticUtil.getInstance().isMystic(e.getResult())) { e.setResult(new ItemStack(Material.AIR)); }
    }

    @EventHandler
    public void prepareItemEnchant(PrepareItemEnchantEvent e) {
        if (mysticUtil.getInstance().isMystic(e.getItem())) { e.setCancelled(true); }
    }

    @EventHandler
    public void dyeArmor(CraftItemEvent e) {
        if (mysticUtil.getInstance().isMystic(e.getCurrentItem())) { e.setCancelled(true); }
    }
}

package me.keegan.handlers;

import me.keegan.utils.mysticUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class mysticHandler implements Listener {
    @EventHandler
    public void prepareAnvil(PrepareAnvilEvent e) {
        if (mysticUtil.getInstance().isMystic(e.getResult())) { e.setResult(new ItemStack(Material.AIR)); }
    }

    @EventHandler
    public void prepareItemEnchant(PrepareItemEnchantEvent e) {
        if (mysticUtil.getInstance().isMystic(e.getItem())) { e.setCancelled(true); }
    }
}

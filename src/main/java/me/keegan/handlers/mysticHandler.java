package me.keegan.handlers;

import me.keegan.utils.mysticUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class mysticHandler implements Listener {
    public void enchantItem(EnchantItemEvent e) {
        if (mysticUtil.getInstance().isMystic(e.getItem())) { e.setCancelled(true); }
    }
}

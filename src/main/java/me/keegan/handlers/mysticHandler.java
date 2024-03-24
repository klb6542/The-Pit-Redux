package me.keegan.handlers;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class mysticHandler implements Listener {
    public void enchantItem(EnchantItemEvent e) {
        ItemStack itemStack = e.getItem();
        if (itemStack.getItemMeta() == null) { return; }

        if (itemStack.getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(ThePitRedux.getPlugin(), "mystic"), PersistentDataType.STRING)) {
            e.setCancelled(true);
        }
    }
}

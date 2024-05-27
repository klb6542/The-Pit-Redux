package me.keegan.vanilla;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class anvilHandler implements Listener {
    List<Material> unavailableForAnvil = new ArrayList<Material>(){{
        add(Material.CHEST);
        add(Material.ENDER_CHEST);
        add(Material.BARREL);
        add(Material.DROPPER);
        add(Material.DISPENSER);
        add(Material.HOPPER);
        add(Material.SHULKER_BOX);
        add(Material.TRAPPED_CHEST);
    }};

    @EventHandler
    public void prepareAnvil(PrepareAnvilEvent e) {
        if (e.getResult() == null || e.getResult().getItemMeta() == null) { return; }

        if (unavailableForAnvil.contains(e.getResult().getType())) { e.setResult(new ItemStack(Material.AIR)); }

        if (e.getResult().getType() == Material.NAME_TAG) {
            ItemStack itemStack = e.getResult();
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName("silent");
            itemStack.setItemMeta(itemMeta);
            e.setResult(itemStack);
        }else if (!e.getInventory().getRenameText().isEmpty()) { e.setResult(new ItemStack(Material.AIR)); }
    }
}

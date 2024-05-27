package me.keegan.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class entityPickUpItemHandler implements Listener {
    // fixes itemstacks that have more than max stack size

    @EventHandler
    public void entityPickUpItem(EntityPickupItemEvent e) {
        ItemStack itemStack = e.getItem().getItemStack();
        if (itemStack.getAmount() <= itemStack.getMaxStackSize()) { return; }

        int amountOverMaxStackSize = itemStack.getAmount() - itemStack.getMaxStackSize();
        itemStack.setAmount(itemStack.getMaxStackSize());

        if (!(e.getEntity() instanceof Player)) { return; }
        Player player = (Player) e.getEntity();

        for (int i = 0; i < amountOverMaxStackSize; i++) {
            ItemStack itemStackClone = itemStack.clone();
            e.getItem().setItemStack(itemStackClone);

            if (player.getInventory().firstEmpty() == -1) {
                itemStackClone.setAmount(amountOverMaxStackSize - i + 1);

                player.getWorld().dropItem(player.getLocation(), itemStackClone);
                return;
            }

            player.getInventory().addItem(itemStackClone);
        }
    }
}

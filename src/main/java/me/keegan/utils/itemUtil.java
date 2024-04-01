package me.keegan.utils;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2024. Created by klb.
 */

public abstract class itemUtil implements Listener {
    public static final List<itemUtil> items = new ArrayList<>();

    public abstract String getNamespaceName();
    public abstract ItemStack createItem();
    public abstract void createRecipe();

    public static void registerItem(itemUtil item) {
       ThePitRedux.getPlugin().getServer().getPluginManager().registerEvents(item, ThePitRedux.getPlugin());

       item.createRecipe();
       items.add(item);
    }
}

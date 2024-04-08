package me.keegan.utils;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Copyright (c) 2024. Created by klb.
 */

public abstract class itemUtil implements Listener {
    public static final List<Material> dyes = new ArrayList<>(Arrays.asList(
            Material.YELLOW_DYE,
            Material.BLACK_DYE,
            Material.BLUE_DYE,
            Material.GREEN_DYE,
            Material.CYAN_DYE,
            Material.GRAY_DYE,
            Material.BROWN_DYE,
            Material.LIGHT_BLUE_DYE,
            Material.LIGHT_GRAY_DYE,
            Material.LIME_DYE,
            Material.MAGENTA_DYE,
            Material.ORANGE_DYE,
            Material.PINK_DYE,
            Material.PURPLE_DYE,
            Material.RED_DYE,
            Material.WHITE_DYE
    ));

    public abstract String getNamespaceName();
    public abstract ItemStack createItem();
    public abstract void createRecipe();

    public static void registerItem(itemUtil item) {
       ThePitRedux.getPlugin().getServer().getPluginManager().registerEvents(item, ThePitRedux.getPlugin());

       item.createRecipe();
    }
}

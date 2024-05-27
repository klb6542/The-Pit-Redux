package me.keegan.vanilla;


import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.mysticUtil;
import me.keegan.utils.setupUtils;
import me.keegan.utils.stringUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class itemHandler implements setupUtils {
    // disables/nerfs enchantHandler, value = level to nerf enchant to
    private final HashMap<Enchantment, Integer> nerfedEnchants = new HashMap<Enchantment, Integer>(){{
        put(Enchantment.DAMAGE_ALL, 1);
        put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        put(Enchantment.ARROW_DAMAGE, 1);
        put(Enchantment.LURE, 1);
        put(Enchantment.LUCK, 1);
        put(Enchantment.DIG_SPEED, 1);
        put(Enchantment.FIRE_ASPECT, 1);
        put(Enchantment.ARROW_FIRE, 1);
        put(Enchantment.ARROW_KNOCKBACK, 1);
    }};

    private final List<Material> bannedMaterials = new ArrayList<>(Arrays.asList(
            Material.SHIELD
    ));

    private void nerfEnchantments(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || mysticUtil.getInstance().isMystic(itemStack)) { continue; }
            Map<Enchantment, Integer> itemEnchants = itemStack.getEnchantments();

            for (Enchantment enchantment : itemEnchants.keySet()) {
                if (!nerfedEnchants.containsKey(enchantment)
                        || itemEnchants.get(enchantment).equals(nerfedEnchants.get(enchantment))) { continue; }

                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.removeEnchant(enchantment);
                itemMeta.addEnchant(enchantment, nerfedEnchants.get(enchantment), false);

                player.sendMessage(red + "" + bold + "NERFED! " + gray
                        + stringUtil.upperCaseFirstLetter(enchantment.getKey().getKey()).replaceAll("_", " ")
                        + " " + integerToRoman(itemEnchants.get(enchantment), false)
                        + " was nerfed to " + stringUtil.upperCaseFirstLetter(enchantment.getKey().getKey()).replaceAll("_", " ")
                        + " " + integerToRoman(itemMeta.getEnchantLevel(enchantment), false) + "!");
                itemStack.setItemMeta(itemMeta);
            }
        }
    }

    private void removeItems(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || !bannedMaterials.contains(itemStack.getType())) { continue; }

            itemStack.setAmount(0);
            player.sendMessage(red + "" + bold + "REMOVED! " + gray + "a banned item in your inventory!");
        }
    }

    @Override
    public void enable() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : ThePitRedux.getPlugin().getServer().getOnlinePlayers()) {
                    nerfEnchantments(player);
                    removeItems(player);
                }
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, 20 * 5);
    }

    @Override
    public void disable() {

    }
}

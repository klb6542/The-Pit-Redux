package me.keegan.items.potions;

import me.keegan.utils.itemUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.itemStackUtil.isSimilar;
import static me.keegan.utils.romanUtil.integerToRoman;

public class jump_boost extends itemUtil {
    private final Integer jumpBoostAmplifier = 3;
    private final Integer jumpBoostDuration = 45;

    private String durationToString() {
        return Math.floorDiv(jumpBoostDuration, 60) + ":" + String.format("%02d", jumpBoostDuration % 60);
    }

    @Override
    public String getNamespaceName() {
        return "jump_boost";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();

        itemMeta.setColor(Color.LIME);
        itemMeta.setDisplayName(green + "Jump Boost " + integerToRoman(jumpBoostAmplifier + 1, false) + " Potion");
        List<String> lore = new ArrayList<>();

        lore.add(gray  + "(" + durationToString() + ")");

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {

    }

    @EventHandler
    public void playerConsumedItem(PlayerItemConsumeEvent e) {
        ItemStack itemStack = e.getItem();
        if (!isSimilar(itemStack, this.createItem())) { return; }

        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, jumpBoostDuration * 20, jumpBoostAmplifier));
    }
}

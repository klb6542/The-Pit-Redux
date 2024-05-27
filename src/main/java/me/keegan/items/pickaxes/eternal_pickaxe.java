package me.keegan.items.pickaxes;

import me.keegan.enchantments.Calidum;
import me.keegan.enums.livesEnums;
import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

public class eternal_pickaxe extends itemUtil {
    private final Integer efficiencyLevel = 2;

    @Override
    public String getNamespaceName() {
        return "eternal_pickaxe";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.NETHERITE_PICKAXE);

        mysticUtil.getInstance().addLives(itemStack, 5, livesEnums.MAX_LIVES);
        mysticUtil.getInstance().addLives(itemStack, 5, livesEnums.LIVES);
        mysticUtil.getInstance().addEnchant(itemStack, new Calidum(), 1, false);

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setUnbreakable(true);
        itemMeta.setDisplayName(darkAqua + "Eternal Pickaxe");

        List<String> lore = itemMeta.getLore();
        lore.add("");
        lore.add(darkAqua + "Efficiency " + integerToRoman(efficiencyLevel, true));

        itemMeta.getPersistentDataContainer().set(new NamespacedKey(ThePitRedux.getPlugin(), "mystic"),
                PersistentDataType.STRING, mysticEnums.PICKAXE.toString().toLowerCase());

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.DIG_SPEED, 2, true);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {

    }
}

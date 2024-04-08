package me.keegan.items.pants;

import me.keegan.builders.mystic;
import me.keegan.enums.mysticEnums;
import me.keegan.items.vile.vile;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.darkPurple;

public class dark_pants extends itemUtil {
    @Override
    public String getNamespaceName() {
        return "dark_pants";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new mystic.Builder()
                .material(Material.LEATHER_LEGGINGS)
                .chatColor(darkPurple)
                .color(Color.BLACK)
                .type(mysticEnums.DARK)
                .build();

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(darkPurple + "Fresh Dark Pants");
        List<String> lore = new ArrayList<>();

        lore.add(mystic.defaultLore.get(0));
        lore.add("");
        lore.add(darkPurple + "Tier I bypasses Mysticism");
        lore.add(darkPurple + "For edgy fashionistas");

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {
        NamespacedKey key = new NamespacedKey(
                ThePitRedux.getPlugin(),
                this.getNamespaceName());

        ShapedRecipe recipe = new ShapedRecipe(key, this.createItem());
        RecipeChoice vile = new RecipeChoice.ExactChoice(new vile().createItem());

        // top - middle - bottom
        recipe.shape("VVV", "V V", "V V");
        recipe.setIngredient('V', vile);

        ThePitRedux.getPlugin().getServer().addRecipe(recipe);
    }
}

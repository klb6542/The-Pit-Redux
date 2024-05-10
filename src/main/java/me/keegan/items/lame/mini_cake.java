package me.keegan.items.lame;

import me.keegan.builders.mystic;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import me.keegan.utils.propertiesUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;

public class mini_cake extends itemUtil {

    @Override
    public String getNamespaceName() {
        return "mini_cake";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.CAKE);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(lightPurple + "Mini Cake");
        List<String> lore = new ArrayList<>();

        lore.add(mystic.defaultLore.get(0));
        lore.add(gray + "The king's favorite!");

        propertiesUtil.setProperty(propertiesUtil.notPlaceable, itemMeta);

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
        RecipeChoice milk_bucket = new RecipeChoice.MaterialChoice(Material.MILK_BUCKET);
        RecipeChoice cherry = new RecipeChoice.ExactChoice(new cherry().createItem());
        RecipeChoice sugar = new RecipeChoice.MaterialChoice(Material.SUGAR);
        RecipeChoice egg = new RecipeChoice.MaterialChoice(Material.EGG);
        RecipeChoice wheat = new RecipeChoice.MaterialChoice(Material.WHEAT);

        // top - middle - bottom
        recipe.shape("CMC", "SES", "WWW");
        recipe.setIngredient('M', milk_bucket);
        recipe.setIngredient('C', cherry);
        recipe.setIngredient('S', sugar);
        recipe.setIngredient('E', egg);
        recipe.setIngredient('W', wheat);

        ThePitRedux.getPlugin().getServer().addRecipe(recipe);
    }
}

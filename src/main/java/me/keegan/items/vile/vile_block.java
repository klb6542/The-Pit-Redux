package me.keegan.items.vile;

import me.keegan.builders.mystic;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import me.keegan.utils.propertiesUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.darkPurple;
import static me.keegan.utils.formatUtil.red;

public class vile_block extends itemUtil {
    @Override
    public String getNamespaceName() {
        return "vile_block";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.COAL_BLOCK);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(darkPurple + "Block of Vile");
        List<String> lore = new ArrayList<>();

        lore.add(mystic.defaultLore.get(0));
        lore.add("");
        lore.add(red + "Heretic artifact");

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
        RecipeChoice vile = new RecipeChoice.ExactChoice(new vile().createItem());

        // top - middle - bottom
        recipe.shape("VVV", "VVV", "VVV");
        recipe.setIngredient('V', vile);

        ThePitRedux.getPlugin().getServer().addRecipe(recipe);
    }

    @EventHandler
    public void furnaceBurn(FurnaceBurnEvent e) {
        if (e.getFuel().isSimilar(this.createItem())) { e.setCancelled(true); }
    }
}

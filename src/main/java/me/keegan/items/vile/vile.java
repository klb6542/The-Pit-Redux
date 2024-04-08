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
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;

public class vile extends itemUtil {
    @Override
    public String getNamespaceName() {
        return "vile";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.COAL);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(darkPurple + "Chunk of Vile");
        List<String> lore = new ArrayList<>();

        lore.add(mystic.defaultLore.get(0));
        lore.add("");
        lore.add(red + "Heretic artifact");

        propertiesUtil.setProperty(propertiesUtil.notBurnable, itemMeta);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {
        NamespacedKey key = new NamespacedKey(
                ThePitRedux.getPlugin(),
                this.getNamespaceName());

        ItemStack vile = this.createItem();
        vile.setAmount(9);

        ShapelessRecipe recipe = new ShapelessRecipe(key, vile);
        RecipeChoice vile_block = new RecipeChoice.ExactChoice(new vile_block().createItem());

        recipe.addIngredient(vile_block);

        ThePitRedux.getPlugin().getServer().addRecipe(recipe);
    }

    @EventHandler
    public void furnaceBurn(FurnaceBurnEvent e) {
        if (e.getFuel().isSimilar(this.createItem())) { e.setCancelled(true); }
    }
}

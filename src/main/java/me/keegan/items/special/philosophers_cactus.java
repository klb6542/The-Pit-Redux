package me.keegan.items.special;

import me.keegan.utils.itemUtil;
import me.keegan.utils.propertiesUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.formatUtil.yellow;

public class philosophers_cactus extends itemUtil {
    @Override
    public String getNamespaceName() {
        return "philosophers_cactus";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.CACTUS);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(green + "Philosopher's Cactus");
        List<String> lore = new ArrayList<>();

        lore.add(yellow + "Special Item");
        lore.add(gray + "Right-click while holding");
        lore.add(gray + "this item to summon fresh");
        lore.add(red + "P" + gold + "a" + yellow + "n" + green + "t" + blue + "s" + gray + " of your");
        lore.add(gray + "choice.");
        lore.add("");
        lore.add(gray + "(Special pants excluded)");

        propertiesUtil.setProperty(propertiesUtil.notBurnable, itemMeta);
        propertiesUtil.setProperty(propertiesUtil.notPlaceable, itemMeta);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {

    }
}

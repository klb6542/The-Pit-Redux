package me.keegan.items.pants;

import me.keegan.builders.mystic;
import me.keegan.enums.mysticEnums;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.keegan.utils.formatUtil.*;

public class aqua_pants extends itemUtil {
    @Override
    public String getNamespaceName() {
        return "aqua_pants";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new mystic.Builder()
                .material(Material.LEATHER_LEGGINGS)
                .chatColor(aqua)
                .color(Color.AQUA)
                .type(mysticEnums.AQUA)
                .build();

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(aqua + "Fresh Aqua Pants");
        List<String> lore = new ArrayList<>();

        lore.add(mystic.defaultLore.get(0));
        lore.add("");
        lore.add(aqua + ChatColor.stripColor(mystic.defaultLore.get(2)));
        lore.add(aqua + "Aqua obtained from fishing!");

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        mysticUtil.getInstance().setMaxTier(itemStack, 1);
        return itemStack;
    }

    @Override
    public void createRecipe() {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerFished(PlayerFishEvent e) {
        int RNG = new Random().nextInt(30);
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH || RNG != 0) { return; }

        Item item = (Item) e.getCaught();
        item.setItemStack(new aqua_pants().createItem());
    }
}

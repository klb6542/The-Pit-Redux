package me.keegan.items.lame;

import me.keegan.builders.mystic;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static me.keegan.utils.formatUtil.*;

public class cherry extends itemUtil {
    private final List<Material> leaves = new ArrayList<>(Arrays.asList(
            Material.OAK_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.ACACIA_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.SPRUCE_LEAVES,
            Material.BIRCH_LEAVES
    ));

    private final Integer randomChanceBound = 500;

    @Override
    public String getNamespaceName() {
        return "cherry";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.APPLE);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(red + "Cherry");
        List<String> lore = new ArrayList<>();

        lore.add(mystic.defaultLore.get(0));
        lore.add(gray + "Cake ingredient");

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {

    }

    @EventHandler
    public void blockBroken(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (!leaves.contains(block.getType())) { return; }

        int randomNumber = new Random().nextInt(randomChanceBound);
        if (randomNumber != 0) { return; }

        block.getWorld().dropItem(block.getLocation(), new cherry().createItem());
    }
}

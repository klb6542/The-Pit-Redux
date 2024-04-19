package me.keegan.items.special;

import me.keegan.builders.mystic;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import me.keegan.utils.propertiesUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;

public class gem extends itemUtil {
    private final String inventoryName = "Totally Legit Selector";
    private final Boolean mysticMustBeTier3 = true;

    public static final String gemIndicator = "♦";

    @Override
    public String getNamespaceName() {
        return "gem";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.EMERALD);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(green + "Totally Legit Gem");
        List<String> lore = new ArrayList<>();

        lore.add(mystic.defaultLore.get(0));
        lore.add("");
        lore.add(gray + "Adds " + lightPurple + "1 tier" + gray + " to a mystic enchant");
        lore.add(darkGray + "Once per item!");
        lore.add("");
        lore.add(yellow + "Hold and right-click to use!");

        propertiesUtil.setProperty(propertiesUtil.notCraftable, itemMeta);

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {

    }

    private String getFooter(ItemStack itemStack) {
        if (mysticUtil.getInstance().getTier(itemStack) < 3) {
            return red + "Item needs to be Tier III!";
        }

        if (mysticUtil.getInstance().isGemmed(itemStack)) {
            return red + "Item has already been upgraded!";
        }

        return yellow + "Click to upgrade!";
    }

    @EventHandler
    public void playerInteracted(PlayerInteractEvent e) {
        if ((e.getAction() != Action.RIGHT_CLICK_AIR
                && e.getAction() != Action.RIGHT_CLICK_BLOCK)
                || e.getItem() == null
                || !e.getItem().isSimilar(this.createItem())) { return; }
        Player player = e.getPlayer();

        // create gem selector inventory
        Inventory inventory = ThePitRedux.getPlugin().getServer().createInventory(
                player,
                36,
                inventoryName);

        List<ItemStack> mystics = mysticUtil.getInstance().getPlayerMystics(player, true, true);

        for (ItemStack itemStack : mystics) {
            String loreFooter = this.getFooter(itemStack);

            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = itemMeta.getLore();
            lore.add("");
            lore.add(loreFooter);

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }

        inventory.addItem(mystics.toArray(new ItemStack[0]));
        player.openInventory(inventory);
    }

    @EventHandler
    public void inventoryClicked(InventoryClickEvent e) {
        InventoryView inventoryView = e.getView();

        if (!inventoryView.getTitle().equals(inventoryName)) { return; }
        e.setCancelled(true);

        Inventory currentInventory = e.getInventory();
        Inventory clickedInventory = e.getClickedInventory();

        if (!currentInventory.equals(clickedInventory)) { return; }
        ItemStack itemStack = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();
        player.closeInventory();

        // create gem inventory
        Inventory inventory = ThePitRedux.getPlugin().getServer().createInventory(
                player,
                36,
                ChatColor.stripColor(this.createItem().getItemMeta().getDisplayName()));

         int enchantCount = mysticUtil.getInstance().getEnchantCount(itemStack);


    }
}

package me.keegan.items.special;

import me.keegan.builders.mystic;
import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import me.keegan.utils.propertiesUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.formatUtil.yellow;
import static me.keegan.utils.itemStackUtil.isSimilar;

public class philosophers_cactus extends itemUtil {
    private final ChatColor philosophersCactusChatColor = green;
    private final String philosophersCactusDisplayName = philosophersCactusChatColor + "Philosopher's Cactus";

    // where the itemstack will go in the inventory
    final int startingIndex = 11;

    @Override
    public String getNamespaceName() {
        return "philosophers_cactus";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.CACTUS);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(philosophersCactusDisplayName);
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
        propertiesUtil.setProperty(propertiesUtil.unavailableForAnvil, itemMeta);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {

    }

    @EventHandler
    public void playerInteracted(PlayerInteractEvent e) {
        ItemStack cactus = this.createItem();

        if ((e.getAction() != Action.RIGHT_CLICK_AIR
                && e.getAction() != Action.RIGHT_CLICK_BLOCK)
                || e.getItem() == null
                || !isSimilar(e.getItem(), cactus)) { return; }
        Player player = e.getPlayer();

        // create inventory
        Inventory inventory = ThePitRedux.getPlugin().getServer().createInventory(
                player,
                27,
                ChatColor.stripColor(cactus.getItemMeta().getDisplayName()));

        int index = startingIndex;

        for (int i = 0; i < mysticUtil.getInstance().defaultPantsColors.size(); i++) {
            ItemStack itemStack = new mystic.Builder()
                    .material(Material.LEATHER_LEGGINGS)
                    .type(mysticEnums.NORMAL)
                    .color(mysticUtil.getInstance().defaultPantsColors.get(i))
                    .chatColor(mysticUtil.getInstance().defaultPantsChatColors.get(i))
                    .build();

            ItemMeta itemMeta = itemStack.getItemMeta();

            List<String> lore = new ArrayList<>();
            lore.add(gray + "Consume the " + philosophersCactusDisplayName.split(" ")[0]);
            lore.add(philosophersCactusChatColor + philosophersCactusDisplayName.split(" ")[1] + gray + " to obtain fresh");
            lore.add(gray + "pants of this color.");
            lore.add("");
            lore.add(yellow + "Click to pet the cactus!");

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(index, itemStack);
            index++;
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void inventoryClicked(InventoryClickEvent e) {
        InventoryView inventoryView = e.getView();
        if (!inventoryView.getTitle().equals(ChatColor.stripColor(this.createItem().getItemMeta().getDisplayName()))) { return; }

        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();

        Inventory currentInventory = e.getInventory();
        Inventory clickedInventory = e.getClickedInventory();
        ItemStack itemStack = e.getCurrentItem();

        if (!currentInventory.equals(clickedInventory)
                || itemStack == null
                || playerInventory.firstEmpty() == -1) { return; }

        ItemStack mainHandItemStack = playerInventory.getItemInMainHand();
        ItemStack offHandItemStack = playerInventory.getItemInOffHand();

        if (isSimilar(mainHandItemStack, this.createItem())) {
            mainHandItemStack.setAmount(mainHandItemStack.getAmount() - 1);
        }else if (isSimilar(offHandItemStack, this.createItem())) {
            offHandItemStack.setAmount(offHandItemStack.getAmount() - 1);
        }else{
            return;
        }

        int index = startingIndex;

        for (int i = 0; i < mysticUtil.getInstance().defaultPantsChatColors.size(); i++) {
            // check if chat colors are the same, if not continue
            if (!itemStack.getItemMeta().getDisplayName().substring(0, 2)
                    .equals(mysticUtil.getInstance().defaultPantsChatColors.get(i).toString())) { index++; continue; }

            ItemStack freshPants = new mystic.Builder()
                    .material(Material.LEATHER_LEGGINGS)
                    .type(mysticEnums.NORMAL)
                    .color(mysticUtil.getInstance().defaultPantsColors.get(i))
                    .chatColor(mysticUtil.getInstance().defaultPantsChatColors.get(i))
                    .build();


            playerInventory.addItem(freshPants);
            break;
        }

        new BukkitRunnable() {

            int count = 0;

            @Override
            public void run() {
                if (count == 6) { this.cancel(); }

                count++;
                player.playSound(player.getLocation(), Sound.ENTITY_SILVERFISH_AMBIENT, 1f, 1f);
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, 2);

        player.closeInventory();
    }
}
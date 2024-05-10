package me.keegan.items.lame;

import me.keegan.builders.mystic;
import me.keegan.enums.mysticEnums;
import me.keegan.items.vile.vile;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import me.keegan.utils.propertiesUtil;
import me.keegan.utils.stringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

import static me.keegan.utils.formatUtil.*;

public class pants_bundle extends itemUtil {
    private final Material fullPantsBundle = Material.CHEST_MINECART;

    @Override
    public String getNamespaceName() {
        return "pants_bundle";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.MINECART);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(aqua + "Pants Bundle");
        List<String> lore = new ArrayList<>();

        lore.add(mystic.defaultLore.get(0));
        lore.add(gray + "Hold and right-click to store 10");
        lore.add(gray + "fresh pair of pants.");

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
        RecipeChoice iron = new RecipeChoice.MaterialChoice(Material.IRON_INGOT);

        // top - middle - bottom
        recipe.shape("   ", "IVI", "III");
        recipe.setIngredient('V', vile);
        recipe.setIngredient('I', iron);

        ThePitRedux.getPlugin().getServer().addRecipe(recipe);
    }

    private int getContentsLoreIndex(List<String> lore) {
        if (lore == null || lore.isEmpty()) { return -1; }

        for (int i = 0; i < lore.size(); i++) {
            if (!lore.get(i).contains("Contents:")) { continue; }

            return i;
        }

        return -1;
    }

    private int getColorIndex(ChatColor chatColor) {
        return (mysticUtil.getInstance().defaultPantsChatColors.contains(chatColor))
                ? (mysticUtil.getInstance().defaultPantsChatColors.indexOf(chatColor))
                : -1;
    }


    private void createFullPantsBundle(Player player, ItemStack itemStack) {
        List<ItemStack> fresh = mysticUtil.getInstance().getPlayerMystics(player, false, false)
                .stream()
                .filter(mystic -> mysticUtil.getInstance().getTier(mystic) == 0)
                .filter(mystic -> mystic.getType() == Material.LEATHER_LEGGINGS)
                .collect(Collectors.toList());
        if (fresh.size() < 10) { return; }

        ItemStack pantsBundle = this.createItem();
        ItemMeta itemMeta = pantsBundle.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add(mystic.defaultLore.get(0));
        lore.add(gray + "Contents:");

        HashMap<String, Integer> freshPants = new HashMap<>();

        // add fresh pants to hashmap
        for (int i = 0; i < 10; i++) {
            String key = fresh.get(i).getItemMeta().getDisplayName().substring(1, 2);

            // remove fresh
            fresh.get(i).setAmount(0);

            freshPants.put(key, 1 + freshPants.getOrDefault(key, 0));
        }

        // retrieve fresh pants from hashmap and place them on lore
        freshPants.forEach(((string, integer) -> lore.add(white + freshPants.get(string).toString()
                + "x " + ChatColor.getByChar(string) + stringUtil.upperCaseFirstLetter(ChatColor.getByChar(string).name()))));

        lore.add("");
        lore.add(gray + "Hold and right-click to open!");

        propertiesUtil.setProperty(propertiesUtil.notPlaceable, itemMeta);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        itemStack.setType(fullPantsBundle);
    }

    @EventHandler
    public void playerInteracted(PlayerInteractEvent e) {
        if ((e.getAction() != Action.RIGHT_CLICK_AIR
                && e.getAction() != Action.RIGHT_CLICK_BLOCK)
                || e.getItem() == null) { return; }
        Player player = e.getPlayer();
        ItemStack unfilledPantsBundle = this.createItem();

        // empty pb
        if (e.getItem().isSimilar(unfilledPantsBundle)) {
            createFullPantsBundle(player, e.getItem());
        }else if(e.getItem().getItemMeta().getDisplayName() // full pb
                .equals(unfilledPantsBundle.getItemMeta().getDisplayName())
                && e.getItem().getType() == fullPantsBundle) {
            ItemStack[] itemStacks = Arrays.stream(player.getInventory().getStorageContents())
                    .filter(Objects::isNull)
                    .toArray(ItemStack[]::new);

            // if the player does not have 10 empty slots return
            ThePitRedux.getPlugin().getLogger().info(itemStacks.length + " length");
            if (itemStacks.length < 10) { return; }

            List<String> lore = e.getItem().getItemMeta().getLore();

            int contentsLoreIndex = getContentsLoreIndex(lore);
            if (contentsLoreIndex == -1) { return; }

            for (int i = contentsLoreIndex + 1; i < lore.size(); i++) {
                // reached end of contents list
                if (lore.get(i).isEmpty()) { break; }

                String[] splitString = lore.get(i).split(" ");

                Integer amountOfFresh = Integer.valueOf(splitString[0].substring(2, splitString[0].length() - 1));
                ChatColor pantsChatColor = ChatColor.getByChar(splitString[1].substring(1, 2));

                int chatColorIndex = getColorIndex(pantsChatColor);
                if (chatColorIndex == -1) { return; }

                // add pants to the player
                for (int j = 0; j < amountOfFresh; j++) {
                    ItemStack pants = new mystic.Builder()
                            .material(Material.LEATHER_LEGGINGS)
                            .type(mysticEnums.NORMAL)
                            .chatColor(pantsChatColor)
                            .color(mysticUtil.getInstance().defaultPantsColors.get(chatColorIndex))
                            .build();

                    player.getInventory().addItem(pants);
                }
            }

            // reset pb to unfilled state
            e.getItem().setType(unfilledPantsBundle.getType());
            e.getItem().setItemMeta(unfilledPantsBundle.getItemMeta());
        }
    }
}

package me.keegan.utils;

import me.keegan.builders.mystic;
import me.keegan.enchantments.*;
import me.keegan.enums.livesEnums;
import me.keegan.enums.mysticEnums;
import me.keegan.items.vile.vile;
import me.keegan.mysticwell.mysticWell;
import me.keegan.pitredux.ThePitRedux;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.*;
import static org.bukkit.Material.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class mysticUtil implements CommandExecutor {
    private static final List<enchantUtil> enchants = new ArrayList<>();
    private static final List<enchantUtil> swordEnchants = new ArrayList<>();
    private static final List<enchantUtil> bowEnchants = new ArrayList<>();
    private static final List<enchantUtil> pantsEnchants = new ArrayList<>();

    public static mysticUtil getInstance() {
        return new mysticUtil();
    }

    private @Nullable List<String> getItemLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) { return null; }

        return (itemMeta.hasLore()) ? itemMeta.getLore() : new ArrayList<>();
    }

    private Integer getEnchantIndex(ItemStack itemStack, enchantUtil enchant, List<String> lore) {
        // returns the index where the enchantment is
        for (int i = 0; i < lore.size(); i++) {
            if (!lore.get(i).contains(blue + enchant.getName())) { continue; }

            return i;
        }

        return -1;
    }

    private Boolean containsEnchant(List<String> lore, Integer loreIndex, Iterator iterator) {
        // used only for getEnchantCount method & getTokens method
        while (iterator.hasNext()) {
            enchantUtil enchant = (enchantUtil) iterator.next();
            if (!lore.get(loreIndex).contains(blue + enchant.getName())) { continue; }

            return true;
        }

        return false;
    }

    public void registerEnchant(enchantUtil enchant) {
        // register listener
        ThePitRedux.getPlugin().getServer().getPluginManager().registerEvents(enchant, ThePitRedux.getPlugin());

        // register enchant to list
        enchants.add(enchant);

        for (int i = 0; i < enchant.getEnchantMaterial().length; i++) {
            // sort the enchants to make it easier for getEnchantCount method
            // don't break because enchants can have multiple materials
            switch (enchant.getEnchantMaterial()[i]) {
                case GOLDEN_SWORD:
                    swordEnchants.add(enchant);
                case BOW:
                    bowEnchants.add(enchant);
                case LEATHER_LEGGINGS:
                    pantsEnchants.add(enchant);
            }

        }
    }

    public List<enchantUtil> getEnchantList() {
        return enchants;
    }

    public Integer getEnchantCount(ItemStack itemStack) {
        List<String> lore = this.getItemLore(itemStack);
        int count = 0;

        if (lore == null || lore.isEmpty()) { return count; }

        for (int i = 0; i < lore.size(); i++) {

            switch (itemStack.getType()) {
                case GOLDEN_SWORD:
                    count += (this.containsEnchant(lore, i, swordEnchants.iterator())) ? 1 : 0;
                    break;
                case BOW:
                    count += (this.containsEnchant(lore, i, bowEnchants.iterator())) ? 1 : 0;
                    break;
                case LEATHER_LEGGINGS:
                    count += (this.containsEnchant(lore, i, pantsEnchants.iterator())) ? 1 : 0;
                    break;
            }

        }

        return count;
    }

    private Integer retrieveEnchantTokens(List<String> lore, Integer enchantIndex) {
        // used only for getTokens method
        String[] splitLore = lore.get(enchantIndex).split(" ");

        return romanToInteger(splitLore[splitLore.length - 1]);
    }

    public Integer getEnchantTokens(ItemStack itemStack) {
        List<String> lore = this.getItemLore(itemStack);
        int tokens = 0;

        if (lore == null || lore.isEmpty()) { return tokens; }

        for (int i = 0; i < lore.size(); i++) {

            switch (itemStack.getType()) {
                case GOLDEN_SWORD:
                    if (!this.containsEnchant(lore, i, swordEnchants.iterator())) { break; }
                    tokens += this.retrieveEnchantTokens(lore, i);
                    break;
                case BOW:
                    if (!this.containsEnchant(lore, i, bowEnchants.iterator())) { break; }
                    tokens += this.retrieveEnchantTokens(lore, i);
                    break;
                case LEATHER_LEGGINGS:
                    if (!this.containsEnchant(lore, i, pantsEnchants.iterator())) { break; }
                    tokens += this.retrieveEnchantTokens(lore, i);
                    break;
            }
        }

        return tokens;
    }

    public Boolean isMystic(ItemStack itemStack) {
        return (itemStack != null
                && itemStack.hasItemMeta()
                && itemStack.getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(ThePitRedux.getPlugin(), "mystic"), PersistentDataType.STRING));
    }

    public Boolean hasEnchant(ItemStack itemStack, enchantUtil enchant) {
        List<String> lore = this.getItemLore(itemStack);

        return lore != null && lore.stream().anyMatch(s -> s.contains(blue + enchant.getName()));
    }

    public void addEnchant(ItemStack itemStack, enchantUtil enchant, Integer enchantLevel) {
        if (this.hasEnchant(itemStack, enchant)) { ThePitRedux.getPlugin().getLogger().info("This enchant is already on the item!"); return; }

        enchantLevel = Math.max(1, Math.min(enchant.getMaxLevel(), enchantLevel)); // fix (basically Math.clamp(1, 3, enchantLevel))
        List<String> lore = this.getItemLore(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();

        // has no itemMeta, return null
        if (lore == null) { return; }

        // if fresh mystic then
        if (lore.contains(mystic.defaultLore.get(0))) {
            lore.clear();
        }

        String name = (enchant.isRareEnchant())
                ? MessageFormat.format("{0}RARE! {1}{2} {3}", lightPurple, blue, enchant.getName(), integerToRoman(enchantLevel, true))
                : MessageFormat.format("{0}{1} {2}", blue, enchant.getName(), integerToRoman(enchantLevel, true));
        String description = enchant.getEnchantDescription()[enchantLevel - 1];

        // do not change this it's okay
        if (!lore.isEmpty()) {
            lore.add("");
        }

        lore.add(name);

        if (description.contains("/n")) {
            lore.addAll(Arrays.asList(description.split("/n")));
        }else {
            lore.add(description);
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public void removeEnchant(ItemStack itemStack, enchantUtil enchant) {
        if (!this.hasEnchant(itemStack, enchant)) { return; }

        List<String> lore = this.getItemLore(itemStack); // already check for item-meta and lore with hasEnchant method
        List<String> newLore = new ArrayList<>();
        ItemMeta itemMeta = itemStack.getItemMeta();

        for (int i = 0; i < lore.size(); i++) {
            if (!lore.get(i).contains(enchant.getName())) { // write old enchant lore to new enchant lore
                newLore.add(newLore.size(), lore.get(i));
                continue;
            }

            for (int j = i; j < lore.size(); j++) { // reaches enchant to skip over
                i++; // skipping

                // stop loop when reaches end of enchantment description
                if (!lore.get(j).isEmpty()) { i++; break; };
            }
        }

        // delete extra space at end of lore
        if (!newLore.isEmpty() && newLore.get(newLore.size() - 1).isEmpty()) {
            newLore.remove(newLore.size() - 1);
        }

        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
    }

    public Integer getEnchantLevel(ItemStack itemStack, enchantUtil enchant) {
        List<String> lore = this.getItemLore(itemStack);
        if (lore == null || lore.isEmpty()) { return 0; }

        Integer enchantIndex = this.getEnchantIndex(itemStack, enchant, lore);
        if (enchantIndex == -1) { return 0; }

        String[] splitLore = lore.get(enchantIndex).split(" ");

        return romanToInteger(splitLore[splitLore.length - 1]);
    }

    private String createLives() {
        return MessageFormat.format("{0}Lives: {1}0{0}/0", gray, red);
    }

    private String createLives(Integer minLives, Integer maxLives) {
        return (minLives <= 3)
                ? MessageFormat.format("{0}Lives: {1}{2}{0}/{3}", gray, red, minLives, maxLives)
                : MessageFormat.format("{0}Lives: {1}{2}{0}/{3}", gray, green, minLives, maxLives);
    }

    /*
    private String getTextFormats(String formattedLives) {
        // returns the color formatting and other text formatting to apply back
        char[] textFormatsArray = formattedLives.toCharArray();

        for (int i = 0; i < textFormatsArray.length; i++) {
            if (textFormatsArray[i] == '§' || (i >= 1 && textFormatsArray[i - 1] == '§')) { continue; }

            textFormatsArray[i] = ' ';
        }

        StringBuilder textFormats = new StringBuilder();

        for (char character : textFormatsArray) {
            // if it reaches the lives stop loop
            if (character == ' ') { break; }

            textFormats.append(character);
        }

        return textFormats.toString();
    }
     */

    public String getLivesFromTextFormats(String formattedLives) {
        // returns the lives from the formattedLives string
        char[] livesArray = formattedLives.toCharArray();

        for (int i = 0; i < livesArray.length; i++) {
            if (livesArray[i] != '§') { continue; }

            livesArray[i] = ' ';
            livesArray[i + 1] = ' ';
        }

        StringBuilder lives = new StringBuilder();

        for (char character : livesArray) {
            if (character == ' ') { continue; }

            lives.append(character);
        }

        return lives.toString();
    }

    public @Nullable String getLives(ItemStack itemStack, livesEnums livesEnum) {
        List<String> lore = this.getItemLore(itemStack);

        // no itemMeta
        if (lore == null || lore.isEmpty()) { return null; }

        String livesText = lore.get(0);
        int indexToGetLives = (livesEnum == livesEnums.LIVES) ? 0 : 1;

        ThePitRedux.getPlugin().getLogger().info(livesText);
        return livesText.split(" ")[1].split("/")[indexToGetLives];
    }

    public void addLives(ItemStack itemStack, Integer lives, livesEnums livesEnum) {
        List<String> lore = this.getItemLore(itemStack);

        // no itemMeta
        if (lore == null) { return; }
        if (lore.isEmpty()) { lore.add(0, this.createLives()); }

        // remove default lore
        if (lore.get(0).equals(mystic.defaultLore.get(0))) {
            lore.clear();
            lore.add(0, this.createLives());
        }

        // if there are enchants but no lives yet, create lives
        if (!lore.get(0).contains(this.createLives().split(" ")[0])) {
            lore.add(0, this.createLives());
            lore.add(1, "");
        }

        // keep lines below to ensure new lore gets added to itemMeta
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        // lives with color/text formatting
        livesEnums oppositeLivesEnum
                = (livesEnum == livesEnums.LIVES)
                ? livesEnums.MAX_LIVES
                : livesEnums.LIVES;
        String formattedLives = this.getLives(itemStack, livesEnum);

        // lives without color/text formatting
        int itemStackLives = Integer.parseInt(this.getLivesFromTextFormats(formattedLives)) + lives;
        int itemStackMaxLives = Integer.parseInt(this.getLivesFromTextFormats(this.getLives(itemStack, oppositeLivesEnum)));


        String finalItemStackLivesLore = (livesEnum == livesEnums.LIVES)
                ? this.createLives(itemStackLives, itemStackMaxLives)
                : this.createLives(itemStackMaxLives, itemStackLives);


        lore.remove(0);
        lore.add(0, finalItemStackLivesLore);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public void removeLives(ItemStack itemStack, Integer lives, livesEnums livesEnum) {
        this.addLives(itemStack, -lives, livesEnum);
        if (Integer.parseInt(this.getLivesFromTextFormats(this.getLives(itemStack, livesEnums.LIVES))) > 0) { return; }

        // destroy itemstack
        itemStack.setAmount(0);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        ItemStack itemStack = new mystic.Builder()
                            .material(GOLDEN_SWORD)
                            .type(mysticEnums.NORMAL)
                            .build();

        // always add the max lives before the lives to avoid issues
        mysticUtil.getInstance().addLives(itemStack, 25, livesEnums.MAX_LIVES);
        mysticUtil.getInstance().addLives(itemStack, 25, livesEnums.LIVES);

        this.addEnchant(itemStack, new Perun(), 3);
        this.addEnchant(itemStack, new SpeedyKill(), 3);
        this.addEnchant(itemStack, new Guts(), 3);
        this.addEnchant(itemStack, new Lifesteal(), 3);

        ItemStack itemStack2 = new mystic.Builder()
                .material(Material.BOW)
                .type(mysticEnums.NORMAL)
                .build();

        mysticUtil.getInstance().addLives(itemStack2, 4, livesEnums.MAX_LIVES);
        mysticUtil.getInstance().addLives(itemStack2, 4, livesEnums.LIVES);

        this.addEnchant(itemStack2, new MegaLongbow(), 3);
        this.addEnchant(itemStack2, new Volley(), 3);

        ItemStack itemStack3 = new mystic.Builder()
                .material(LEATHER_LEGGINGS)
                .type(mysticEnums.NORMAL)
                .color(Color.LIME)
                .chatColor(green)
                .build();

        mysticUtil.getInstance().addEnchant(itemStack3, new Solitude(), 3);
        mysticUtil.getInstance().addEnchant(itemStack3, new Hearts(), 3);

        mysticUtil.getInstance().addLives(itemStack3, 500, livesEnums.MAX_LIVES);
        mysticUtil.getInstance().addLives(itemStack3, 500, livesEnums.LIVES);

        ItemStack itemStack4 = new mysticWell().createItem();

        ItemStack itemStack5 = new vile().createItem();
        itemStack5.setAmount(64);

        ThePitRedux.getPlugin().getServer().getPlayer("qsmh").getInventory().addItem(itemStack, itemStack2, itemStack3, itemStack4, itemStack5);
        return true;
    }
}
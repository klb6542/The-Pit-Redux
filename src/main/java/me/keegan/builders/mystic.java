package me.keegan.builders;

import me.keegan.classes.Tier;
import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.tierUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class mystic {
     // do not include a lives builder because this is only supposed to build fresh

    public static List<String> defaultLore = new ArrayList<>(Arrays.asList(
            gray + "Kept on death",
            "",
            gray + "Used in the mystic well"
    ));

    private static void setDefaultItemMeta(ItemMeta itemMeta) {
        itemMeta.setUnbreakable(true);

        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
    }

    private static ItemStack buildSword(ItemStack itemStack, Builder builder) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        setDefaultItemMeta(itemMeta);

        itemMeta.setDisplayName(yellow + "Mystic Sword");
        itemMeta.setLore(defaultLore);

        // 7.5 attack damage
        itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private static ItemStack buildBow(ItemStack itemStack, Builder builder) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        setDefaultItemMeta(itemMeta);

        itemMeta.setDisplayName(aqua + "Mystic Bow");
        itemMeta.setLore(defaultLore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private static ItemStack buildPants(ItemStack itemStack, Builder builder) {
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        setDefaultItemMeta(itemMeta);

        itemMeta.setColor(builder.color);
        itemMeta.setDisplayName(builder.chatColor + "Fresh Dyed Pants");

        List<String> lore = new ArrayList<>();

        for (int i = 0; i < defaultLore.size(); i++) {
            if (i != defaultLore.size() - 1) {
                lore.add(defaultLore.get(i));
                continue;
            }

            // splits last index of lore, and gets only the text and not the color, so
            // it can be recolored to the builder.chatColor instead of gray
            lore.add(builder.chatColor + defaultLore.get(i).split(gray.toString())[1]);
            lore.add(builder.chatColor + "Also, a fashion statement");
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private static ItemStack buildMystic(@NotNull Builder builder) {
        ItemStack itemStack = new ItemStack(builder.material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(new NamespacedKey(ThePitRedux.getPlugin(), "mystic"),
                PersistentDataType.STRING, builder.type.toString().toLowerCase());

        // set itemMeta back to itemStack to make sure its fully updated
        itemStack.setItemMeta(itemMeta);

        switch (builder.material) {
            case GOLDEN_SWORD:
                return mystic.buildSword(itemStack, builder);
            case BOW:
                return mystic.buildBow(itemStack, builder);
            case LEATHER_LEGGINGS:
                return mystic.buildPants(itemStack, builder);
        }

        return new ItemStack(Material.AIR);
    }

    public static class Builder {
        private Material material;
        private mysticEnums type;

        private Color color;
        private ChatColor chatColor;

        public Builder() {

        }

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public Builder type(mysticEnums mysticEnum) {
            this.type = mysticEnum;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder chatColor(ChatColor chatColor) {
            this.chatColor = chatColor;
            return this;
        }

        public ItemStack build() {
            return buildMystic(this);
        }
    }

    // other classes

    public static class sword implements tierUtil {
        @Override
        public @Nullable ChatColor getColorFromTier(int tier) {
            return normalColorTiers.get(tier);
        }

        @Override
        public @Nullable Material getColorPaneFromTier(int tier) {
            return normalPaneColorTiers.get(tier);
        }
    }

    public static class bow implements tierUtil {
        @Override
        public @Nullable ChatColor getColorFromTier(int tier) {
            return normalColorTiers.get(tier);
        }

        @Override
        public @Nullable Material getColorPaneFromTier(int tier) {
            return normalPaneColorTiers.get(tier);
        }
    }

    public static class pants {
        private static ItemStack pants;

        public pants(ItemStack itemStack) {
            pants = (itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName().length() - 1 > 2)
                    ? itemStack
                    : null;
        }

        public static class normal implements tierUtil {
            @Override
            public @Nullable ChatColor getColorFromTier(int tier) {
                return (pants != null)
                        ? ChatColor.getByChar(pants.getItemMeta().getDisplayName().substring(1, 2))
                        : null;
            }

            @Override
            public @Nullable Material getColorPaneFromTier(int tier) {
                return normalPaneColorTiers.get(tier);
            }
        }

        public static class dark implements tierUtil {
            @Override
            public @Nullable ChatColor getColorFromTier(int tier) {
                return (pants != null)
                        ? ChatColor.getByChar(pants.getItemMeta().getDisplayName().substring(1, 2))
                        : null;
            }

            @Override
            public @Nullable Material getColorPaneFromTier(int tier) {
                return darkPaneColorTiers.get(tier);
            }
        }
    }
}

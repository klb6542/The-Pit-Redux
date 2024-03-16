package me.keegan.utils;

/*
 * Copyright (c) 2024. Created by klb.
 */

public abstract class enchantUtil {
    public enum enchantTypes {
        SWORD,
        BOW,
        PANTS
    }

    public abstract enchantTypes[] getEnchantType();
    public abstract String getName();
    public abstract String getEnchantName();
    public abstract String[] getEnchantDescription();
    public abstract Number getMaxLevel();
    public abstract boolean isRareEnchant();
    public abstract void procEnchant();
}

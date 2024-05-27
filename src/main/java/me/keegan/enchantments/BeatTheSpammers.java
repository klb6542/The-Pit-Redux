package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;

public class BeatTheSpammers extends enchantUtil {
    @Override
    public Material[] getEnchantMaterial() {
        return new Material[0];
    }

    @Override
    public mysticEnums getEnchantType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[0];
    }

    @Override
    public Integer getMaxLevel() {
        return null;
    }

    @Override
    public boolean isRareEnchant() {
        return false;
    }

    @Override
    public boolean isMysticWellEnchant() {
        return false;
    }

    @Override
    public void executeEnchant(Object[] args) {

    }
}

package me.keegan.enchantments;

import me.keegan.utils.enchantUtil;
import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Guts extends enchantUtil {

    @Override
    public enchantTypes[] getEnchantType() {
        return new enchantTypes[]{enchantTypes.SWORD};
    }

    @Override
    public String getName() {
        return "Guts";
    }

    @Override
    public String getEnchantName() {
        return this.getName();
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Heal {1}0.25❤{0} on kill", gray, red),
                MessageFormat.format("{0}Heal {1}0.5❤{0} on kill", gray, red),
                MessageFormat.format("{0}Heal {1}1❤{0} on kill", gray, red)
        };
    }

    @Override
    public Number getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isRareEnchant() {
        return false;
    }

    @Override
    public void procEnchant() {
        // write enchantment code here
    }
}

package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffectType;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

public class PurpleCaffeine extends enchantUtil {
    private final Integer[] hasteDurationPerLevel = new Integer[]{2, 4, 6};
    private final Integer[] hasteAmplifierPerLevel = new Integer[]{0, 1, 3};
    private final Integer[] regenerationDurationPerLevel = new Integer[]{1, 2, 3};
    private final Integer regenerationAmplifier = 2;

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.LEATHER_LEGGINGS};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.NORMAL;
    }

    @Override
    public String getName() {
        return "Purple Caffeine";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Gain {1}Haste {3} {0}({4}s) and {2}Regen {5}/n" +
                        "{0}({6}s) from breaking obsidian", gray, yellow, red,
                        integerToRoman(hasteAmplifierPerLevel[0] + 1, false), hasteDurationPerLevel[0],
                        integerToRoman(regenerationAmplifier + 1, false), regenerationDurationPerLevel[0]),

                MessageFormat.format("{0}Gain {1}Haste {3} {0}({4}s) and {2}Regen {5}/n" +
                                "{0}({6}s) from breaking obsidian", gray, yellow, red,
                        integerToRoman(hasteAmplifierPerLevel[1] + 1, false), hasteDurationPerLevel[1],
                        integerToRoman(regenerationAmplifier + 1, false), regenerationDurationPerLevel[1]),

                MessageFormat.format("{0}Gain {1}Haste {3} {0}({4}s) and {2}Regen {5}/n" +
                                "{0}({6}s) from breaking obsidian", gray, yellow, red,
                        integerToRoman(hasteAmplifierPerLevel[2] + 1, false), hasteDurationPerLevel[2],
                        integerToRoman(regenerationAmplifier + 1, false), regenerationDurationPerLevel[2])
        };
    }

    @Override
    public Integer getMaxLevel() {
        return this.getEnchantDescription().length;
    }

    @Override
    public boolean isRareEnchant() {
        return false;
    }

    @Override
    public boolean isMysticWellEnchant() {
        return true;
    }

    @Override
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];

        BlockBreakEvent e = (BlockBreakEvent) args[0];
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if (block.getType() != Material.OBSIDIAN) { return; }

        this.addPotionEffect(player, PotionEffectType.REGENERATION,
                regenerationAmplifier, regenerationDurationPerLevel[enchantLevel].doubleValue());
        this.addPotionEffect(player, PotionEffectType.FAST_DIGGING,
                hasteAmplifierPerLevel[enchantLevel], hasteDurationPerLevel[enchantLevel].doubleValue());
    }

    @EventHandler
    public void blockBroken(BlockBreakEvent e) {
        Player player = e.getPlayer();

        Object[] args = new Object[]{
                e,
                (player.getEquipment() != null) ? player.getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(args);
    }
}

package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Steaks extends enchantUtil {
    private final String[] steakTiers = new String[]{"good", "great", "outstanding"};
    private final Integer[] absorptionHeartsPerLevel = new Integer[]{1, 2, 3};
    private final Integer[] strengthDurationPerLevel = new Integer[]{3, 5, 7};
    private final Integer[] regenerationDurationPerLevel = new Integer[]{2, 3, 4};

    private final Integer strengthAmplifier = 0;
    private final Integer regenerationAmplifier = 0;

    // used only for checking if itemstack is similar to steak

    private Boolean isSimilar(ItemStack itemStack, ItemStack itemStack2) {
        ItemStack copyItemStack = itemStack.clone();
        ItemStack copyItemStack2 = itemStack2.clone();

        ItemMeta itemMeta = copyItemStack.getItemMeta();
        ItemMeta itemMeta2 = copyItemStack2.getItemMeta();

        if (itemMeta != null && itemMeta.getLore() != null) {
            List<String> lore = itemMeta.getLore();
            lore.remove(0);

            itemMeta.setLore(lore);
        }

        if (itemMeta2 != null && itemMeta2.getLore() != null) {
            List<String> lore = itemMeta2.getLore();
            lore.remove(0);

            itemMeta2.setLore(lore);
        }

        copyItemStack.setItemMeta(itemMeta);
        copyItemStack2.setItemMeta(itemMeta2);

        return copyItemStack.isSimilar(copyItemStack2);
    }

    private ItemStack createSteak(int enchantLevel) {
        ItemStack itemStack = new ItemStack(Material.COOKED_BEEF);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(darkGreen + "Steak");

        List<String> lore = new ArrayList<>();
        lore.add(gray + "Steaks are tasty and have " + steakTiers[enchantLevel] + " fibers");
        lore.add(gray + "for your muscles");

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void procSteakEffects(Player player, ItemStack itemStack) {
        String startingLore = itemStack.getItemMeta().getLore().get(0);
        int enchantLevel = 0;

        for (int i = 0; i < steakTiers.length; i++) {
            if (!startingLore.contains(steakTiers[i])) { continue; }

            enchantLevel = i;
        }

        if (enchantLevel == 0 ) { return; }

        this.addPotionEffect(player, PotionEffectType.INCREASE_DAMAGE, strengthAmplifier, strengthDurationPerLevel[enchantLevel].doubleValue());
        this.addPotionEffect(player, PotionEffectType.REGENERATION, regenerationAmplifier, regenerationDurationPerLevel[enchantLevel].doubleValue());

        if (player.getAbsorptionAmount() >= absorptionHeartsPerLevel[enchantLevel] * 2) { return; }
        player.setAbsorptionAmount((absorptionHeartsPerLevel[enchantLevel] * 2 - player.getAbsorptionAmount()) + player.getAbsorptionAmount());
    }

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
        return "Steaks";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Gain a {1}steak{0} on kill. {1}Steaks{0} are/n"
                        + "{0}tasty and have {3} fibers/n"
                        + "{0}for your muscles./n"
                        + "{0}{2}Steaks do not fulfill your hunger.", gray, darkGreen, italic, steakTiers[0]),

                MessageFormat.format("{0}Gain a {1}steak{0} on kill. {1}Steaks{0} are/n"
                        + "{0}tasty and have {3} fibers/n"
                        + "{0}for your muscles./n"
                        + "{0}{2}Steaks do not fulfill your hunger.", gray, darkGreen, italic, steakTiers[1]),

                MessageFormat.format("{0}Gain a {1}steak{0} on kill. {1}Steaks{0} are/n"
                        + "{0}tasty and have {3} fibers/n"
                        + "{0}for your muscles./n"
                        + "{0}{2}Steaks do not fulfill your hunger.", gray, darkGreen, italic, steakTiers[2]),
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

        EntityDeathEvent e = (EntityDeathEvent) args[0];
        Player killer = e.getEntity().getKiller();

        // full inventory
        if (killer.getInventory().firstEmpty() == -1){ return; }
        killer.getInventory().addItem(this.createSteak(enchantLevel));
    }

    @EventHandler
    public void entityDied(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) { return; }
        Player killer = e.getEntity().getKiller();

        Object[] args = new Object[]{
                e,
                (killer.getEquipment() != null) ? killer.getEquipment().getLeggings() : null,
                this,
        };

       this.attemptEnchantExecution(args);
    }

    @EventHandler
    public void playerConsumedItem(PlayerItemConsumeEvent e) {
        // immutable
        ItemStack itemStack = e.getItem();
        if (!isSimilar(itemStack, this.createSteak(0))) { return; }

        Player player = e.getPlayer();
        player.setFoodLevel(player.getFoodLevel() - 8);

        this.procSteakEffects(player, itemStack);
    }
}

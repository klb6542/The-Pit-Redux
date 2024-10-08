package me.keegan.mysticwell;

import me.keegan.builders.mystic;
import me.keegan.enchantments.Inspire;
import me.keegan.enums.livesEnums;
import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.mysticUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/*
 * Copyright (c) 2024. Created by klb.
 */

// very helpful! - klb
// https://www.educative.io/answers/a-list-of-all-the-functional-interfaces-in-java

public class algorithm {
    /*
     * do not attempt to clean up the lists in the algorithm
     * they are put there in a specific "noob" way so the enchants are updated correctly
     */

    private final HashMap<Integer, Runnable> tierMethods = new HashMap<Integer, Runnable>(){{
        put(0, () -> addEnchantToTierOne());
        put(1, () -> addEnchantToTierTwo());
        put(2, () -> addEnchantToTierThree());
    }};

    private final HashMap<mysticEnums, algorithmUtil> algorithms = new HashMap<mysticEnums, algorithmUtil>(){{
       put(mysticEnums.AQUA, new aqua_algorithm());
    }};

    private final Integer maxEnchantCount = 3;

    private Player player;
    private ItemStack itemStack;

    algorithm(Player player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

    void run() {
        this.player.giveExpLevels(
                -mysticWell.xpLevelsEnchantCostPerTier.getOrDefault(mysticUtil.getInstance().getTier(this.itemStack), 0)
        );

        int itemStackTier = mysticUtil.getInstance().getTier(this.itemStack);

        mysticEnums mysticType = mystic.getMysticType(this.itemStack);
        if (mysticType == null) { return; }

        if (algorithms.containsKey(mysticType)) {
            algorithms.get(mysticType).run(this.player, this.itemStack, itemStackTier);
        }else{
            this.tierMethods.get(itemStackTier).run();
        }
    }

    private boolean shouldGiveRareEnchant(int minChance, int maxChance) {
        return new Random().nextInt(maxChance) + 1 < minChance + 1;
    }

    private void addEnchantToTierOne() {
        int RNG = new Random().nextInt(11);
        int enchantCount = mysticUtil.getInstance().getEnchantCount(this.itemStack);

        if (enchantCount >= maxEnchantCount) {
            return;
        }

        if (RNG > 4) {
            mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomNonRareEnchant(this.itemStack), 1, true);
        } else if (RNG > 1) {
            mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomNonRareEnchant(this.itemStack), 2, true);
        }else {
            mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomNonRareEnchant(this.itemStack), 1, true);
            mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomNonRareEnchant(this.itemStack), 1, true);
        }

        int randomLives = new Random().nextInt(5 - 4) + 4;

        mysticUtil.getInstance().addTier(this.itemStack, 1);
        mysticUtil.getInstance().addLives(this.itemStack, randomLives, livesEnums.MAX_LIVES);
        mysticUtil.getInstance().addLives(this.itemStack, randomLives, livesEnums.LIVES);
    }

    private void addEnchantToTierTwo() {
        int RNG = new Random().nextInt(15);
        int enchantCount = mysticUtil.getInstance().getEnchantCount(this.itemStack);

        if (enchantCount >= maxEnchantCount) {
            return;
        }

        if (RNG > 5) {
            int RNGTwo = new Random().nextInt(5);

            if (shouldGiveRareEnchant(1, 30)) {
                mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomRareEnchant(this.itemStack), new Random().nextInt(2) + 1, true);
            }else{
                mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomNonRareEnchant(this.itemStack), new Random().nextInt(2) + 1, true);
            }

            if (RNGTwo < 2) {
                // filter out max enchants from non maxed enchants
                List<enchantUtil> itemStackEnchants = mysticUtil.getInstance().getEnchants(this.itemStack)
                        .stream()
                        .filter(enchantUtil -> !mysticUtil.getInstance().getEnchantTokens(this.itemStack, enchantUtil).equals(enchantUtil.getMaxLevel()))
                        .collect(Collectors.toList());

                mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), 1);
            }
        } else if (RNG > 3) {
            for (int i = enchantCount; i < maxEnchantCount; i++) {
                if (shouldGiveRareEnchant(1, 15)) {
                    mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomRareEnchant(this.itemStack), new Random().nextInt(2) + 1, true);
                }else{
                    mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomNonRareEnchant(this.itemStack), new Random().nextInt(2) + 1, true);
                }
            }
        }else { // upgrade an enchant
            int RNGTwo = new Random().nextInt(9);

            // filter out max enchants from non maxed enchants
            List<enchantUtil> itemStackEnchants = mysticUtil.getInstance().getEnchants(this.itemStack)
                    .stream()
                    .filter(enchantUtil -> !mysticUtil.getInstance().getEnchantTokens(this.itemStack, enchantUtil).equals(enchantUtil.getMaxLevel()))
                    .collect(Collectors.toList());

            // upgrade 1 enchant
            if (RNGTwo > 0) {
                mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), new Random().nextInt(2) + 1);
            }else{ // upgrade 2 enchants
                if (enchantCount == 1) {
                    mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), 1);
                }else {
                    mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), new Random().nextInt(2) + 1);
                    mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), 1);
                }
            }
        }

        int randomLives = new Random().nextInt(3) + 1;

        mysticUtil.getInstance().addTier(this.itemStack, 1);
        mysticUtil.getInstance().addLives(this.itemStack, randomLives, livesEnums.MAX_LIVES);
        mysticUtil.getInstance().addLives(this.itemStack, randomLives, livesEnums.LIVES);
    }

    private void addEnchantToTierThree() {
        int RNG = new Random().nextInt(18);
        int enchantCount = mysticUtil.getInstance().getEnchantCount(this.itemStack);

        if (enchantCount >= maxEnchantCount) {
            if (RNG > 7) {
                int RNGTwo = new Random().nextInt(2);

                List<enchantUtil> itemStackEnchants = mysticUtil.getInstance().getEnchants(this.itemStack)
                        .stream()
                        .filter(enchantUtil -> !mysticUtil.getInstance().getEnchantTokens(this.itemStack, enchantUtil).equals(enchantUtil.getMaxLevel()))
                        .collect(Collectors.toList());

                if (itemStackEnchants.isEmpty()) {
                    // no clue why I did this just ignore it makes no sense
                    mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomEnchant(this.itemStack), 1, true);
                }else {
                    mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), 1);

                    if (RNGTwo == 0) {
                        mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), 1);
                    }
                }
            }else{
                int RNGTwo = new Random().nextInt(4);

                List<enchantUtil> itemStackEnchants = mysticUtil.getInstance().getEnchants(this.itemStack)
                        .stream()
                        .filter(enchantUtil -> !mysticUtil.getInstance().getEnchantTokens(this.itemStack, enchantUtil).equals(enchantUtil.getMaxLevel()))
                        .collect(Collectors.toList());

                if (itemStackEnchants.isEmpty()) {
                    // no clue why I did this just ignore it makes no sense
                    mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomEnchant(this.itemStack), new Random().nextInt(2) + 1, true);
                }else{
                    mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), 2);

                    if (RNGTwo == 0) {
                        mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), new Random().nextInt(2) + 1);
                    }
                }
            }
        }else{
            if (RNG > 5) {
                int RNGTwo = new Random().nextInt(4);
                mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomEnchant(this.itemStack), new Random().nextInt(2) + 1, true);

                if (RNGTwo == 0 && mysticUtil.getInstance().getTier(this.itemStack) != 8) {
                    // don't need to check if itemStackEnchants is empty because there is already a new enchant
                    List<enchantUtil> itemStackEnchants = mysticUtil.getInstance().getEnchants(this.itemStack)
                            .stream()
                            .filter(enchantUtil -> !mysticUtil.getInstance().getEnchantTokens(this.itemStack, enchantUtil).equals(enchantUtil.getMaxLevel()))
                            .collect(Collectors.toList());

                    mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), 1);
                }
            }else{
                if (enchantCount == 1) {
                    for (int i = enchantCount; i < maxEnchantCount; i++) {
                        if (shouldGiveRareEnchant(1, 13)) {
                            mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomRareEnchant(this.itemStack), new Random().nextInt(2) + 1, true);
                        }else {
                            mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomNonRareEnchant(this.itemStack), new Random().nextInt(2) + 1, true);
                        }
                    }
                }else{
                    int RNGTwo = new Random().nextInt(3);

                    List<enchantUtil> itemStackEnchants = mysticUtil.getInstance().getEnchants(this.itemStack)
                            .stream()
                            .filter(enchantUtil -> !mysticUtil.getInstance().getEnchantTokens(this.itemStack, enchantUtil).equals(enchantUtil.getMaxLevel()))
                            .collect(Collectors.toList());

                    if (itemStackEnchants.isEmpty()) {
                        mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomEnchant(this.itemStack), new Random().nextInt(2) + 1, true);
                    }else{
                        mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), new Random().nextInt(2) + 1);

                        if (RNGTwo == 0) {
                            mysticUtil.getInstance().addEnchantLevel(this.itemStack, itemStackEnchants.get(new Random().nextInt(itemStackEnchants.size())), new Random().nextInt(2) + 1);
                        }
                    }
                }
            }
        }

        int randomLives = new Random().nextInt(5) + 1;

        mysticUtil.getInstance().addTier(this.itemStack, 1);
        mysticUtil.getInstance().addLives(this.itemStack, randomLives, livesEnums.MAX_LIVES);
        mysticUtil.getInstance().addLives(this.itemStack, randomLives, livesEnums.LIVES);
    }

    public static class aqua_algorithm implements algorithmUtil {
        private final Integer maxEnchantCount = 2;

        private Player player;
        private ItemStack itemStack;

        @Override
        public void run(Player player, ItemStack itemStack, int tier) {
            if (tier != 0) { return; }

            this.player = player;
            this.itemStack = itemStack;

            addEnchantToTierOne();
        }

        private void addEnchantToTierOne() {
            int RNG = new Random().nextInt(3);
            int enchantCount = mysticUtil.getInstance().getEnchantCount(this.itemStack);

            if (enchantCount >= maxEnchantCount) {
                return;
            }

            mysticUtil.getInstance().addEnchant(this.itemStack, new Inspire(), 1, false);

            if (RNG > -1) {
                mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomNonRareEnchant(this.itemStack), 1, true);
            }else{
                mysticUtil.getInstance().addEnchant(this.itemStack, mysticUtil.getInstance().getRandomRareEnchant(this.itemStack), 1, true);
            }

            mysticUtil.getInstance().addTier(this.itemStack, 1);
            mysticUtil.getInstance().addLives(this.itemStack, 5, livesEnums.MAX_LIVES);
            mysticUtil.getInstance().addLives(this.itemStack, 5, livesEnums.LIVES);
        }
    }
}

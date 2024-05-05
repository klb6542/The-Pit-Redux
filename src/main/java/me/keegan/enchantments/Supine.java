package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.setupUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Supine extends enchantUtil implements setupUtils {
    private static final HashMap<UUID, BukkitTask> runnables = new HashMap<>();
    private static final HashMap<UUID, Integer> supinePantsEquipped = new HashMap<>();

    private final Integer[] heartsMinimumPerLevel = new Integer[]{9, 8, 7};
    private final Integer[] hasteAmplifierPerLevel = new Integer[]{0, 0, 1};
    private final Integer[] speedAmplifierPerLevel = new Integer[]{-1, 0, 0};
    private final Integer[] luckAmplifierPerLevel = new Integer[]{-1, -1, 3};

    private final long timerPeriod = 20L;

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
        return "Supine";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Gain {2}Haste {4}{0} when above {1}{3}❤{0}",
                        gray, red, yellow, heartsMinimumPerLevel[0],
                        integerToRoman(hasteAmplifierPerLevel[0] + 1, false)),

                MessageFormat.format("{0}Gain {2}Haste {5}{0} and {3}Speed {6}{0} when/n"
                                + "{0} above {1}{4}❤{0}",
                        gray, red, yellow, blue, heartsMinimumPerLevel[1],
                        integerToRoman(hasteAmplifierPerLevel[1] + 1, false),
                        integerToRoman(speedAmplifierPerLevel[1] + 1, false)),

                MessageFormat.format("{0}Gain {4}Luck {8}{0}, {2}Haste {6}{0}, and/n"
                                + "{3}Speed {7}{0} when above {1}{5}❤{0}",
                        gray, red, yellow, blue, green, heartsMinimumPerLevel[2],
                        integerToRoman(hasteAmplifierPerLevel[2] + 1, false),
                        integerToRoman(speedAmplifierPerLevel[2] + 1, false),
                        integerToRoman(luckAmplifierPerLevel[2] + 1, false))
        };
    }

    @Override
    public Integer getMaxLevel() {
        return this.getEnchantDescription().length;
    }

    @Override
    public boolean isRareEnchant() {
        return true;
    }

    @Override
    public boolean isMysticWellEnchant() {
        return true;
    }

    @Override
    public void executeEnchant(Object[] args) {
        if (args.length == 4) { return; }
        int enchantLevel = (int) args[2];

        PlayerJoinEvent e = (PlayerJoinEvent) args[0];
        Player player = e.getPlayer();

        this.addPotionEffect(player, PotionEffectType.FAST_DIGGING, hasteAmplifierPerLevel[enchantLevel], 3.0);

        // if enchant level is tier 1 return
        if (enchantLevel < 1) { return; }
        this.addPotionEffect(player, PotionEffectType.SPEED, speedAmplifierPerLevel[enchantLevel], 3.0);

        // if enchant level is tier 2 or lower return
        if (enchantLevel < 2) { return; }
        this.addPotionEffect(player, PotionEffectType.LUCK, luckAmplifierPerLevel[enchantLevel], 3.0);
    }

    private void removeSupine(Player player) {
        UUID uuid = player.getUniqueId();

        if (!supinePantsEquipped.containsKey(uuid)) { return; }
        supinePantsEquipped.remove(uuid);
    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity) || !(e.getDamager() instanceof Player)) { return; }

        LivingEntity damager = (LivingEntity) e.getDamager();

        Object[] args = new Object[]{
                e,
                (damager.getEquipment() != null) ? damager.getEquipment().getLeggings() : null,
                this,
        };

        this.attemptEnchantExecution(args);
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        final Supine thisSupine = this;

        runnables.put(uuid, new BukkitRunnable() {

            @Override
            public void run() {
                if (player.getEquipment() == null || player.getEquipment().getLeggings() == null) {
                    thisSupine.removeSupine(player);
                    return;
                }

                Object[] args = new Object[]{
                        e,
                        player.getEquipment().getLeggings(),
                        thisSupine,
                        true
                };

                boolean success = attemptEnchantExecution(args);
                if (success || !supinePantsEquipped.containsKey(uuid)) { return; } // if enchant was successfully executed or pants are not equipped return

                thisSupine.removeSupine(player);
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, timerPeriod));
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        this.removeSupine(player);
        if (!runnables.containsKey(uuid)) { return; }

        runnables.get(uuid).cancel();
        runnables.remove(uuid);
    }

    @Override
    public void enable() {
        for (Player player : ThePitRedux.getPlugin().getServer().getOnlinePlayers()) {
            PlayerJoinEvent e = new PlayerJoinEvent(
                    player,
                    null
            );

            ThePitRedux.getPlugin().getServer().getPluginManager().callEvent(e);
        }
    }

    @Override
    public void disable() {
        for (Player player : ThePitRedux.getPlugin().getServer().getOnlinePlayers()) {
            this.removeSupine(player);
        }
    }
}

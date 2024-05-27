package me.keegan.enchantments;

/*
 * Copyright (c) 2024. Created by klb.
 */

import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.setupUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;

public class BooBoo extends enchantUtil implements setupUtils {
    private static final HashMap<UUID, BukkitTask> runnables = new HashMap<>();
    private static final HashMap<UUID, Integer> boobooPantsEquipped = new HashMap<>();
    private static final List<UUID> boobooInProgress = new ArrayList<>();

    private final Integer[] cooldownSecondsPerLevel = new Integer[]{5, 4, 3};
    private final Integer heartsToRegain = 1;

    private final long timerPeriod = 10;

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
        return "Boo-boo";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Passively regain {1}{2}❤ {0}every {3}/n"
                        + "{0}seconds", gray, red, heartsToRegain, cooldownSecondsPerLevel[0]),

                MessageFormat.format("{0}Passively regain {1}{2}❤ {0}every {3}/n"
                        + "{0}seconds", gray, red, heartsToRegain, cooldownSecondsPerLevel[1]),

                MessageFormat.format("{0}Passively regain {1}{2}❤ {0}every {3}/n"
                        + "{0}seconds", gray, red, heartsToRegain, cooldownSecondsPerLevel[2])
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

        PlayerJoinEvent e = (PlayerJoinEvent) args[0];
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (boobooInProgress.contains(uuid) || args.length == 4 || runnables.containsKey(uuid)) { return; }
        boobooInProgress.add(uuid);

        final BooBoo thisBooBoo = this;

        new BukkitRunnable() {

            @Override
            public void run() {
                Object[] args = new Object[]{
                        e,
                        player.getEquipment().getLeggings(),
                        thisBooBoo,
                        true
                };

                boolean success = attemptEnchantExecution(player, args);

                if (success) {
                    thisBooBoo.heal(player, heartsToRegain * 2.0);
                }

                boobooInProgress.remove(uuid);
            }

        }.runTaskLater(ThePitRedux.getPlugin(), enchantLevel * 20L);
    }

    private void removeBooBoo(Player player) {
        if (!boobooPantsEquipped.containsKey(player.getUniqueId())) { return; }

        boobooPantsEquipped.remove(player.getUniqueId());
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        final BooBoo thisBooBoo = this;

        runnables.put(uuid, new BukkitRunnable() {

            @Override
            public void run() {
                // if player has no pants on remove extra max hearts if they had any and return
                if (player.getEquipment() == null || player.getEquipment().getLeggings() == null) {
                    thisBooBoo.removeBooBoo(player);
                    return;
                }

                Object[] args = new Object[]{
                        e,
                        player.getEquipment().getLeggings(),
                        thisBooBoo
                };

                boolean success = attemptEnchantExecution(null, args);
                if (success || !boobooPantsEquipped.containsKey(uuid)) { return; } // if enchant was successfully executed or pants are not equipped return

                thisBooBoo.removeBooBoo(player);
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, timerPeriod));
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        this.removeBooBoo(player);

        if (runnables.containsKey(uuid)) {
            runnables.get(uuid).cancel();
            runnables.remove(uuid);
        }

        boobooInProgress.remove(uuid);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        for (Player player : ThePitRedux.getPlugin().getServer().getOnlinePlayers()) {
            this.removeBooBoo(player);
        }
    }
}

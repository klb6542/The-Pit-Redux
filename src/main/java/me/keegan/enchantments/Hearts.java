package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;

public class Hearts extends enchantUtil {
    private static final HashMap<UUID, BukkitTask> runnables = new HashMap<>();
    private static final HashMap<UUID, Integer> heartsPantsEquipped = new HashMap<>();

    private final Integer[] healthMaxPerLevel = new Integer[]{1, 2, 4};

    private final long timerPeriod = 5L;

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
        return "Hearts";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Increase your max health by {1}{2}❤", gray, red, (double) healthMaxPerLevel[0] / 2),
                MessageFormat.format("{0}Increase your max health by {1}{2}❤", gray, red, (double) healthMaxPerLevel[1] / 2),
                MessageFormat.format("{0}Increase your max health by {1}{2}❤", gray, red, (double) healthMaxPerLevel[2] / 2),
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
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];

        PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) args[0];
        Player player = playerJoinEvent.getPlayer();
        UUID uuid = player.getUniqueId();

        if (heartsPantsEquipped.containsKey(uuid)) { return; }

        // add extra max hearts
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + healthMaxPerLevel[enchantLevel]
        );

        heartsPantsEquipped.put(uuid, enchantLevel);
    }

    private void removeExtraMaxHearts(Player player) {
        UUID uuid = player.getUniqueId();
        if (!heartsPantsEquipped.containsKey(uuid)) { return; }

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - healthMaxPerLevel[heartsPantsEquipped.get(uuid)]
        );

        heartsPantsEquipped.remove(uuid);
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        final Hearts thisHearts = this;

        runnables.put(player.getUniqueId(), new BukkitRunnable() {

            @Override
            public void run() {
                // if player has no pants on remove extra max hearts if they had any and return
                if (player.getEquipment() == null || player.getEquipment().getLeggings() == null) {
                    thisHearts.removeExtraMaxHearts(player);
                    return;
                }

                Object[] args = new Object[]{
                        e,
                        player.getEquipment().getLeggings(),
                        thisHearts
                };

                boolean success = attemptEnchantExecution(args);
                if (success || !heartsPantsEquipped.containsKey(uuid)) { return; } // if enchant was successfully executed or pants are not equipped return

                thisHearts.removeExtraMaxHearts(player);
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, timerPeriod));
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        this.removeExtraMaxHearts(player);

        runnables.get(uuid).cancel();
        runnables.remove(uuid);
    }
}

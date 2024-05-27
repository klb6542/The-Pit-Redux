package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

public class GuardiansAura extends enchantUtil {
    private static final HashMap<UUID, BukkitTask> runnables = new HashMap<>();
    private static final List<UUID> guardiansAuraPantsEquipped = new ArrayList<>();

    private final Integer regenAmplifier = 0;
    private final Integer luckAmplifier = 0;
    private final Integer blockRange = 6;

    private final long timerPeriod = 5L;

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.LEATHER_LEGGINGS};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.AQUA;
    }

    @Override
    public String getName() {
        return "Guardian's Aura";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Gain {1}Regen {3}{0} and {2}Luck {4}{0} when/n"
                        + "{0}near water",
                        gray, red, green, integerToRoman(regenAmplifier + 1, false),
                        integerToRoman(luckAmplifier + 1, false))
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
        PlayerJoinEvent e = (PlayerJoinEvent) args[0];
        Player player = e.getPlayer();
        if (!this.isNearWater(player.getLocation(), blockRange)) { return; }

        this.addPotionEffect(player, PotionEffectType.REGENERATION, regenAmplifier, 3.0);
        this.addPotionEffect(player, PotionEffectType.LUCK, luckAmplifier, 3.0);

        guardiansAuraPantsEquipped.add(player.getUniqueId());
    }

    private void removeExtraMaxHearts(Player player) {
        if (!guardiansAuraPantsEquipped.contains(player.getUniqueId())) { return; }

        guardiansAuraPantsEquipped.remove(player.getUniqueId());
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        // return if there is one currently running, so it doesn't duplicate
        if (runnables.containsKey(uuid)) { return; }

        final GuardiansAura thisGuardianAura = this;

        runnables.put(uuid, new BukkitRunnable() {

            @Override
            public void run() {
                // if player has no pants on remove extra max hearts if they had any and return
                if (player.getEquipment() == null || player.getEquipment().getLeggings() == null) {
                    thisGuardianAura.removeExtraMaxHearts(player);
                    return;
                }

                Object[] args = new Object[]{
                        e,
                        player.getEquipment().getLeggings(),
                        thisGuardianAura
                };

                boolean success = attemptEnchantExecution(player, args);
                if (success || !guardiansAuraPantsEquipped.contains(uuid)) { return; } // if enchant was successfully executed or pants are not equipped return

                thisGuardianAura.removeExtraMaxHearts(player);
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, timerPeriod));
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        this.removeExtraMaxHearts(player);
        if (!runnables.containsKey(uuid)) { return; }

        runnables.get(uuid).cancel();
        runnables.remove(uuid);
    }
}

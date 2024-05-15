package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.setupUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
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

public class GottaGoFast extends enchantUtil implements setupUtils {
    // GGF is additive not multiplicative

    private static final HashMap<UUID, BukkitTask> runnables = new HashMap<>();
    private static final HashMap<UUID, Integer> ggfPantsEquipped = new HashMap<>();
    private final Float[] movementIncreasePerLevel = new Float[]{0.1f, 0.2f, 0.3f};

    private final long timerPeriod = 1L;

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
        return "Gotta Go Fast";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Move {1}{2}% faster{0} at all times", gray, yellow, movementIncreasePerLevel[0] * 100),
                MessageFormat.format("{0}Move {1}{2}% faster{0} at all times", gray, yellow, movementIncreasePerLevel[1] * 100),
                MessageFormat.format("{0}Move {1}{2}% faster{0} at all times", gray, yellow, movementIncreasePerLevel[2] * 100)
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

        PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) args[0];
        Player player = playerJoinEvent.getPlayer();
        UUID uuid = player.getUniqueId();

        if (ggfPantsEquipped.containsKey(uuid)) {
            // https://www.spigotmc.org/threads/comprehensive-particle-spawning-guide-1-13-1-19.343001/
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 1, 0, 0.2, 0, 0.075);
            return;
        }

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(
               player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() + movementIncreasePerLevel[enchantLevel] * 0.1
       );

        ggfPantsEquipped.put(uuid, enchantLevel);
    }

    private void removeExtraMovementSpeed(Player player) {
        UUID uuid = player.getUniqueId();
        if (!ggfPantsEquipped.containsKey(uuid)) { return; }

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() - movementIncreasePerLevel[ggfPantsEquipped.get(uuid)] * 0.1
        );

        ggfPantsEquipped.remove(uuid);
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (runnables.containsKey(uuid)) { return; }

        final GottaGoFast thisGGF = this;

        runnables.put(uuid, new BukkitRunnable() {

            @Override
            public void run() {
                if (player.getEquipment() == null || player.getEquipment().getLeggings() == null) {
                    thisGGF.removeExtraMovementSpeed(player);
                    return;
                }

                Object[] args = new Object[]{
                        e,
                        player.getEquipment().getLeggings(),
                        thisGGF
                };

                boolean success = attemptEnchantExecution(args);
                if (success || !ggfPantsEquipped.containsKey(uuid)) { return; } // if enchant was successfully executed or pants are not equipped return

                thisGGF.removeExtraMovementSpeed(player);
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, timerPeriod));
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        this.removeExtraMovementSpeed(player);
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
            this.removeExtraMovementSpeed(player);
        }
    }
}

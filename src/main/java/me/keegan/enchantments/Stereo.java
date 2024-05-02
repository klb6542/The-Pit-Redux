package me.keegan.enchantments;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
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

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Stereo extends enchantUtil implements setupUtils {
    private static final HashMap<UUID, BukkitRunnable> runnables = new HashMap<>();
    private static final List<Player> stereoPantsEquipped = new ArrayList<>();

    private static Playlist stereoPlaylist;

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
        return "Stereo";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}You play a tune while cruising/n" +
                        "{0}around", gray)
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
        return false;
    }

    @Override
    public void executeEnchant(Object[] args) {
        PlayerJoinEvent e = (PlayerJoinEvent) args[0];
        Player player = e.getPlayer();

        stereoPantsEquipped.add(player);

        ThePitRedux.getPlugin().getLogger().info(stereoPlaylist.getCount() + " songs");
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        runnables.put(player.getUniqueId(), new BukkitRunnable() {

            @Override
            public void run() {
                if (player.getEquipment() == null
                        || player.getEquipment().getLeggings() == null
                        || stereoPantsEquipped.contains(player)) { return; }

                Object[] args = new Object[]{
                        e,
                        player.getEquipment().getLeggings(),
                        this
                };

                attemptEnchantExecution(args);
            }

        }).runTaskTimer(ThePitRedux.getPlugin(), 0, timerPeriod);
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        stereoPantsEquipped.remove(player);

        runnables.get(player).cancel();
        runnables.remove(player);
    }

    @Override
    public void enable() {
        File songsFolder = new File(ThePitRedux.getPlugin().getDataFolder().getAbsolutePath(), "songs");
        if (songsFolder.exists()) { return; }

        // create sound folder if it does not exist
        songsFolder.mkdirs();
    }

    @Override
    public void disable() {

    }
}

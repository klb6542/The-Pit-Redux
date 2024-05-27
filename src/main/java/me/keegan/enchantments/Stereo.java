package me.keegan.enchantments;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.model.SoundCategory;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class Stereo extends enchantUtil {
    private static final HashMap<UUID, BukkitTask> runnables = new HashMap<>();
    private static final HashMap<UUID, Integer> stereoPantsEquipped = new HashMap<>();
    private static final HashMap<UUID, EntitySongPlayer> songsPlaying = new HashMap<>();

    private final long timerPeriod = 1L;

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
        UUID uuid = player.getUniqueId();

        Playlist stereoPlaylist = getStereroPlaylist();
        if (stereoPlaylist == null) { ThePitRedux.getPlugin().getLogger().info(red + "There are no songs for Stereo to play!"); return; }
        if (stereoPantsEquipped.containsKey(uuid)) { return; }

        EntitySongPlayer entitySongPlayer = new EntitySongPlayer(stereoPlaylist);
        entitySongPlayer.setCategory(SoundCategory.RECORDS);
        entitySongPlayer.setRepeatMode(RepeatMode.ALL);
        entitySongPlayer.setEntity(player);
        entitySongPlayer.addPlayer(player);
        entitySongPlayer.setDistance(16);
        entitySongPlayer.setRandom(true);
        entitySongPlayer.setPlaying(true);

        songsPlaying.put(uuid, entitySongPlayer);
        stereoPantsEquipped.put(uuid, 1);
    }

    private Playlist getStereroPlaylist() {
        File songsFolder = new File(ThePitRedux.getPlugin().getDataFolder().getAbsolutePath(), "songs");

        if (songsFolder.exists()
                && songsFolder.listFiles() != null
                && songsFolder.listFiles().length > 0) {
            List<Song> songs = new ArrayList<>();

            Arrays.stream(songsFolder.listFiles())
                    .forEach(file -> songs.add(NBSDecoder.parse(file)));

            Playlist stereoPlaylist = new Playlist(songs.get(0));

            songs.remove(0);
            songs.forEach(stereoPlaylist::add);

            return stereoPlaylist;
        }

        // create sound folder if it does not exist
        songsFolder.mkdirs();
        return null;
    }

    private void removeStereoPants(Player player) {
        UUID uuid = player.getUniqueId();

        if (stereoPantsEquipped.containsKey(uuid)) {
            stereoPantsEquipped.remove(uuid);
        }

        if (songsPlaying.containsKey(uuid)) {
            songsPlaying.get(uuid).destroy(); // stops the song and destroys object
            songsPlaying.remove(uuid);
        }
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (runnables.containsKey(uuid)) { return; }

        final Stereo thisStereo = this;

        runnables.put(uuid, new BukkitRunnable() {

            @Override
            public void run() {
                if (player.getEquipment() == null || player.getEquipment().getLeggings() == null) {
                    thisStereo.removeStereoPants(player);
                    return;
                }

                Object[] args = new Object[]{
                        e,
                        player.getEquipment().getLeggings(),
                        thisStereo
                };

                boolean success = attemptEnchantExecution(null, args);
                if (success || !stereoPantsEquipped.containsKey(uuid)) { return; } // if enchant was successfully executed or pants are not equipped return

                thisStereo.removeStereoPants(player);
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, timerPeriod));
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        this.removeStereoPants(player);
        if (!runnables.containsKey(uuid)) { return; }

        runnables.get(uuid).cancel();
        runnables.remove(uuid);
    }
}

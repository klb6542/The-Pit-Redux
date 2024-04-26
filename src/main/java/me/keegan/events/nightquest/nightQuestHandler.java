package me.keegan.events.nightquest;

/*
 * Copyright (c) 2024. Created by klb.
 */

import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.setupUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class nightQuestHandler implements Listener, setupUtils {
    private static final HashMap<UUID, nightQuestModel> activeNightQuests = new HashMap<>();
    private static final List<UUID> completedNightQuests = new ArrayList<>();

    private nightQuestEnums getRandomNightQuestType() {
        return nightQuestEnums.values()[new Random().nextInt(nightQuestEnums.values().length)];
    }

    private nightQuestModel createRandomNightQuest(Player player) {
        return new nightQuestModel(player, this.getRandomNightQuestType(), 0);
    }

    public void giveNightQuest(Player player) {
        nightQuestModel nightQuest = this.createRandomNightQuest(player);

        activeNightQuests.put(player.getUniqueId(), nightQuest);
    }

    @Override
    public void enable() {
        new BukkitRunnable() {

            @Override
            public void run() {
                ThePitRedux plugin = ThePitRedux.getPlugin();

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    World world = player.getWorld();
                    UUID uuid = player.getUniqueId();

                    ThePitRedux.getPlugin().getLogger().info(player.getDisplayName());

                    if (world.getEnvironment() != World.Environment.NORMAL
                            || activeNightQuests.containsKey(uuid)
                            || completedNightQuests.contains(uuid)) { return; }

                    boolean isDayTime = world.getTime() < 13000 || world.getTime() > 23000;

                    if (isDayTime) {
                        completedNightQuests.removeIf(completedNightQuests::contains);
                        return;
                    }

                    new nightQuestHandler().giveNightQuest(player);
                }
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, 20);
    }

    @Override
    public void disable() {

    }
}

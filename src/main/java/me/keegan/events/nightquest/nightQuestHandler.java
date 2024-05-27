package me.keegan.events.nightquest;

/*
 * Copyright (c) 2024. Created by klb.
 */

import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.setupUtils;
import me.keegan.utils.stringUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.stringUtil.*;

public class nightQuestHandler implements Listener, setupUtils {
    private static final HashMap<UUID, nightQuestModel> activeNightQuests = new HashMap<>();
    private static final List<UUID> completedNightQuests = new ArrayList<>();
    private static final HashMap<nightQuestEnums, HashMap<Object, Integer[]>> nightQuestArgs = new HashMap<>(nightQuestEnums.values().length);

    private void initializeNightQuestArgs() {

        /*
         * key = material type target
         * values = min and max values for random number target
         *
         * values must have a size of exactly 2 elements
         */

        nightQuestArgs.put(nightQuestEnums.KILL, new HashMap<Object, Integer[]>(){{
            put(EntityType.ZOMBIE, new Integer[]{15, 21});
            put(EntityType.SKELETON, new Integer[]{15, 21});
            put(EntityType.CREEPER, new Integer[]{10, 16});
            put(EntityType.SPIDER, new Integer[]{10, 16});
            put(EntityType.ENDERMAN, new Integer[]{3, 6});
        }});

        nightQuestArgs.put(nightQuestEnums.MINE, new HashMap<Object, Integer[]>(){{
            put(Material.STONE, new Integer[]{100, 201});
            put(Material.DIRT, new Integer[]{100, 201});
            put(Material.SAND, new Integer[]{100, 201});
        }});

        nightQuestArgs.put(nightQuestEnums.FISH, new HashMap<Object, Integer[]>(){{
            put(Material.COD, new Integer[]{10, 16});
            put(Material.SALMON, new Integer[]{10, 16});
            put(Material.TROPICAL_FISH, new Integer[]{5, 11});
            put(Material.PUFFERFISH, new Integer[]{3, 6});
        }});
    }

    private Boolean receivedNightQuest(UUID uuid) {
        return completedNightQuests.contains(uuid) || activeNightQuests.containsKey(uuid);
    }

    private Boolean isInOverWorld(World world) {
        return world.getEnvironment() == World.Environment.NORMAL;
    }

    private nightQuestEnums getRandomNightQuestType() {
        return nightQuestEnums.values()[new Random().nextInt(nightQuestEnums.values().length)];
    }

    private nightQuestModel createRandomNightQuest(Player player) {
        if (nightQuestArgs.isEmpty()) { return null; }
        nightQuestEnums nightQuestType = this.getRandomNightQuestType();

        HashMap<Object, Integer[]> args = nightQuestArgs.get(nightQuestType);
        Object[] keyArgsArray = args.keySet().toArray();

        Object target = keyArgsArray[new Random().nextInt(keyArgsArray.length)];
        Integer minRandom = args.get(target)[0];
        Integer maxRandom = args.get(target)[1];

        Integer requiredProgress = new Random().nextInt(maxRandom - minRandom) + minRandom;

        return new nightQuestModel(player, nightQuestType, target, requiredProgress);
    }

    private void addNightQuestProgress(nightQuestModel nightQuest, UUID uuid, Integer progressAmount) {
        nightQuest.addProgress(progressAmount);
        if (nightQuest.getProgress() < nightQuest.getRequiredProgress()) { return; }

        activeNightQuests.remove(uuid);
        completedNightQuests.add(uuid);
        nightQuest.nightQuestComplete();
    }

    public void giveNightQuest(Player player) {
        nightQuestModel nightQuest = this.createRandomNightQuest(player);
        if (nightQuest == null) { return; }

        player.sendMessage(
                blue + "" + bold + "NIGHT QUEST! "
                        + gray + stringUtil.upperCaseFirstLetter(nightQuest.getNightQuestType().toString())
                        + red + " " + nightQuest.getRequiredProgress()
                        + gray + " " + getPluralWord(nightQuest.getTargetName()).toLowerCase()
        );

        activeNightQuests.put(player.getUniqueId(), nightQuest);
    }

    // main methods

    @EventHandler
    public void entityDied(EntityDeathEvent e) {
        LivingEntity killed = e.getEntity();
        LivingEntity killer = killed.getKiller();
        UUID uuid = (killer != null) ? killer.getUniqueId() : null;
        if (killed == null
                || !activeNightQuests.containsKey(uuid)
                || activeNightQuests.get(uuid).getNightQuestType() != nightQuestEnums.KILL
                || !isInOverWorld(killer.getWorld())) { return; }

        nightQuestModel nightQuest = activeNightQuests.get(uuid);
        EntityType target = (EntityType) nightQuest.getTarget();
        if (killed.getType() != target) { return; }

       this.addNightQuestProgress(nightQuest, uuid, 1);
    }

    @EventHandler
    public void blockBroke(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        UUID uuid = player.getUniqueId();
        if (!activeNightQuests.containsKey(uuid)
                || activeNightQuests.get(uuid).getNightQuestType() != nightQuestEnums.MINE
                || !isInOverWorld(player.getWorld())) { return; }

        nightQuestModel nightQuest = activeNightQuests.get(uuid);
        Material target = (Material) nightQuest.getTarget();
        if (block.getType() != target) { return; }

        this.addNightQuestProgress(nightQuest, uuid, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerFished(PlayerFishEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (e.getCaught() == null
                || e.getState() != PlayerFishEvent.State.CAUGHT_FISH
                || !activeNightQuests.containsKey(uuid)
                || activeNightQuests.get(uuid).getNightQuestType() != nightQuestEnums.FISH
                || !isInOverWorld(player.getWorld())) { return; }

        Item item = (Item) e.getCaught();
        ItemStack itemStack = item.getItemStack();

        nightQuestModel nightQuest = activeNightQuests.get(uuid);
        Material target = (Material) nightQuest.getTarget();
        if (itemStack.getType() != target) { return; }

        this.addNightQuestProgress(nightQuest, uuid, itemStack.getAmount());
    }

    @Override
    public void enable() {
        this.initializeNightQuestArgs();

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : ThePitRedux.getPlugin().getServer().getOnlinePlayers()) {
                    World world = player.getWorld();
                    UUID uuid = player.getUniqueId();

                    boolean isDayTime = world.getTime() < 13000 || world.getTime() > 23000;

                    if (world.getEnvironment() == World.Environment.NORMAL && isDayTime) {
                        completedNightQuests.removeIf(completedNightQuests::contains);
                        if (!activeNightQuests.containsKey(uuid)) { return; }

                        nightQuestModel nightQuest = activeNightQuests.get(uuid);

                        player.sendMessage(blue + "" + bold + "NIGHT QUEST! " + gray + "Ran out of time! " + blue +  "("
                                + nightQuest.getProgress() + "/" + nightQuest.getRequiredProgress() + ")");
                        activeNightQuests.remove(uuid);
                        return;
                    }

                    if (world.getEnvironment() != World.Environment.NORMAL
                            || receivedNightQuest(uuid)) { return; }

                    new nightQuestHandler().giveNightQuest(player);
                }
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, 20);
    }

    @Override
    public void disable() {

    }
}

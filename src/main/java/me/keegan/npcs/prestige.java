package me.keegan.npcs;

import com.mongodb.client.MongoCollection;
import me.keegan.global.mongodb;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.npcUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.romanUtil.integerToRoman;

public class prestige extends npcUtil {
    private static prestige instance;

    public static prestige getInstance() {
        if (instance == null) {
            instance = new prestige();
        }

        return instance;
    }

    @Override
    public String getNPCName() {
        return "Prestige";
    }

    @Override
    public void npcTriggered(Player player, NPC npc) {
        MongoCollection<Document> collection = mongodb.getInstance().getSurvivalCollection();
        if (collection == null) { return; }

        Document playerDocument =  mongodb.getInstance().getDocumentFromCollection(collection,
                mongodb.getInstance().createDefaultPlayerDocument(player));
        if (playerDocument == null) { return; }

        Inventory inventory = ThePitRedux.getPlugin().getServer().createInventory(player, 27, prestigeHandler.getInstance().inventoryName);
        prestigeHandler.getInstance().createPrestigeItem(inventory, playerDocument);
        renownShopHandler.getInstance().createRenownShopItem(inventory, playerDocument);

        player.openInventory(inventory);
    }

    // HANDLES ALL PRESTIGING
    public static class prestigeHandler implements Listener {
        private static prestigeHandler instance;

        public static prestigeHandler getInstance() {
            if (instance == null) {
                instance = new prestigeHandler();
            }

            return instance;
        }

        private final List<Integer> requiredLevelsToPrestige = new ArrayList<>(Arrays.asList(
                250,
                300,
                350,
                400,
                450,
                525,
                600,
                675,
                750,
                825,
                925,
                1025
        ));

        private final List<Integer> renownRewardPerPrestige = new ArrayList<>(Arrays.asList(
                5,
                5,
                5,
                5,
                10,
                10,
                10,
                10,
                10,
                15,
                15,
                15
        ));

        private static final HashMap<UUID, BukkitTask> runnables = new HashMap<>();
        int maxPrestigeAllowed = 5;

        private final String inventoryName = "Prestige & Renown";
        private final String inventoryName2 = "Prestige?";

        private String getCurrentPrestige(Document document) {
            return (!integerToRoman(document.getInteger("prestige"), false).isEmpty())
                    ? yellow + integerToRoman(document.getInteger("prestige"), false)
                    : red + "No prestige!";
        }

        private String getNextPrestige(Document document) {
            Integer nextPrestige = document.getInteger("prestige") + 1;

            return (nextPrestige <= maxPrestigeAllowed)
                    ? yellow + integerToRoman(nextPrestige, false)
                    : red + "Max prestige!";
        }

        private String getRequiredLevel(Document document) {
            Integer requiredLevel = (document.getInteger("prestige") < maxPrestigeAllowed)
                    ? requiredLevelsToPrestige.get(document.getInteger("prestige"))
                    : Integer.MAX_VALUE;

            return (requiredLevel != Integer.MAX_VALUE)
                    ? aqua + new DecimalFormat("#,###").format(requiredLevel) + " XP Levels"
                    : aqua.toString() + bold + "MAXED OUT!!!";
        }

        private String getRenownReward(Document document) {
            Integer renownReward = (document.getInteger("prestige") < maxPrestigeAllowed)
                    ? renownRewardPerPrestige.get(document.getInteger("prestige"))
                    : Integer.MAX_VALUE;

            return (renownReward != Integer.MAX_VALUE)
                    ? yellow.toString() + renownReward + " Renown"
                    : red + "None!";
        }

        private String getPrestigeFooter(Player player, Document document) {
            return (canPrestige(player, document))
                    ? yellow + "Click to purchase!"
                    : red + "Missing requirements!";
        }

        private String getConfirmationPrestigeFooter(boolean isWaitTimeOver) {
            return (!isWaitTimeOver)
                    ? yellow + "Wait and read!"
                    : yellow + "Click to prestige!";
        }

        private boolean canPrestige(Player player, Document document) {
            return player.getLevel() >= requiredLevelsToPrestige.get(document.getInteger("prestige"))
                    && document.getInteger("prestige") < maxPrestigeAllowed;
        }

        private void createPrestigeItem(Inventory inventory, Document document) {
            ItemStack itemStack = new ItemStack(Material.DIAMOND);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(aqua + "Prestige");

            List<String> lore = new ArrayList<>();
            lore.add(gray + "Current: " + getCurrentPrestige(document));
            lore.add(gray + "Required: " + getRequiredLevel(document));
            lore.add("");
            lore.add(gray + "Costs:");
            lore.add(red.toString() + bold + "◼ Resets " + aqua + "level " + red + "to 0");
            lore.add(red.toString() + bold + "◼ Resets " + red + "all " + green + "perks & upgrades");
            //lore.add(red.toString() + bold + "◼ Grinded " + aqua + "level" + red + "to 1");
            lore.add(gray.toString() + italic + "Renown upgrades are kept.");
            lore.add("");
            lore.add(gray + "Reward: " + getRenownReward(document));
            lore.add(gray + "New prestige: " + getNextPrestige(document));
            lore.add("");
            lore.add(getPrestigeFooter(ThePitRedux.getPlugin().getServer()
                            .getPlayer(UUID.fromString(document.getString("uuid"))),
                    document));

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(11, itemStack);
        }

        private void createConfirmationPrestigeItem(Inventory inventory, Document document, boolean isWaitTimeOver) {
            ItemStack itemStack = new ItemStack((!isWaitTimeOver) ? Material.YELLOW_CONCRETE : Material.GREEN_CONCRETE);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(green + "ARE YOU SURE?");

            List<String> lore = new ArrayList<>();
            lore.add(gray + "New prestige: " + yellow + integerToRoman(document.getInteger("prestige") + 1, false));
            lore.add("");
            lore.add(red.toString() + bold + "RESETTING LEVEL!");
            lore.add(red.toString() + bold + "RESETTING PERKS!");
            lore.add(red.toString() + bold + "RESETTING UPGRADES!");
            lore.add("");
            lore.add((getConfirmationPrestigeFooter(isWaitTimeOver)));

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(11, itemStack);
        }

        private void createCancellationPrestigeItem(Inventory inventory) {
            ItemStack itemStack = new ItemStack(Material.RED_CONCRETE);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(red + "CANCEL!");

            List<String> lore = new ArrayList<>();
            lore.add(gray + "Go back to menu");

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(15, itemStack);
        }

        // normal prestige inventory
        @EventHandler
        public void inventoryClicked(InventoryClickEvent e) {
            if (!e.getView().getTitle().equals(inventoryName)) { return; }
            e.setCancelled(true);

            if (!e.getInventory().equals(e.getClickedInventory())) { return; }

            MongoCollection<Document> collection = mongodb.getInstance().getSurvivalCollection();
            if (collection == null) { return; }

            Player player = (Player) e.getWhoClicked();
            UUID uuid = player.getUniqueId();

            Document playerDocument =  mongodb.getInstance().getDocumentFromCollection(collection,
                    mongodb.getInstance().createDefaultPlayerDocument(player));
            if (playerDocument == null) { return; }

            if (e.getSlot() == 11 && canPrestige(player, playerDocument)) {
                if (runnables.containsKey(uuid)) {
                    runnables.get(uuid).cancel();
                    runnables.remove(uuid);
                }

                runnables.put(player.getUniqueId(), new BukkitRunnable() {
                    final AtomicInteger countdown = new AtomicInteger(6);

                    @Override
                    public void run() {
                        if (!player.getOpenInventory().getTitle().startsWith(inventoryName2)
                                && runnables.containsKey(uuid)
                                && countdown.get() != 6) {
                            this.cancel();
                            runnables.remove(uuid);

                            return;
                        }

                        countdown.decrementAndGet();

                        String confirmationInventoryName = inventoryName2 + " (" + countdown.get() + ")";
                        boolean isWaitTimeOver = false;

                        if (countdown.get() == 0) {
                            this.cancel();
                            runnables.remove(uuid);

                            confirmationInventoryName = inventoryName2;
                            isWaitTimeOver = true;
                        }

                        Inventory inventory = ThePitRedux.getPlugin().getServer().createInventory(player, 27, confirmationInventoryName);
                        createConfirmationPrestigeItem(inventory, playerDocument, isWaitTimeOver);
                        createCancellationPrestigeItem(inventory);

                        player.openInventory(inventory);
                    }

                }.runTaskTimer(ThePitRedux.getPlugin(), 0, 20));
            }

            if (e.getSlot() == 15) {
                player.sendMessage(red + "Coming soon!");
            }
        }

        // prestige confirmation inventory
        @EventHandler
        public void inventoryClicked2(InventoryClickEvent e) {
            if (!e.getView().getTitle().startsWith(inventoryName2)) { return; }
            e.setCancelled(true);

            if (!e.getInventory().equals(e.getClickedInventory())) { return; }
            Player player = (Player) e.getWhoClicked();

            if (e.getSlot() == 11 && !runnables.containsKey(player.getUniqueId())) {
                MongoCollection<Document> collection = mongodb.getInstance().getSurvivalCollection();
                if (collection == null) { return; }

                Document playerDocument =  mongodb.getInstance().getDocumentFromCollection(collection,
                        mongodb.getInstance().createDefaultPlayerDocument(player));
                if (playerDocument == null) { return; }
                player.closeInventory();

                playerDocument.replace("perks", new ArrayList<>());
                playerDocument.replace("upgrades", new ArrayList<>());
                playerDocument.replace("prestige", playerDocument.getInteger("prestige") + 1);
                playerDocument.replace("renown", playerDocument.getInteger("renown") + renownRewardPerPrestige.get(
                        playerDocument.getInteger("prestige") - 1));

                mongodb.getInstance().replaceDocumentFromCollection(collection, playerDocument);

                player.setLevel(0);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 2f, 1f);

                String prestigeInRomanNumerals = integerToRoman(playerDocument.getInteger("prestige"), false);

                player.sendTitle(yellow.toString() + bold + "PRESTIGE!",
                        gray + "You unlocked prestige " + yellow + prestigeInRomanNumerals,
                        20,
                        100,
                        20
                );

                ThePitRedux.getPlugin().getServer().broadcastMessage(yellow.toString() + bold + "PRESTIGE! "
                        + player.getDisplayName() + gray
                        + " unlocked prestige " + yellow + prestigeInRomanNumerals
                        + gray + ", gg!");
            }

            if (e.getSlot() == 15) {
                prestige.getInstance().npcTriggered(player, null);
            }
        }
    }

    // HANDLES ALL RENOWN PURCHASES
    public static class renownShopHandler implements Listener {
        private static renownShopHandler instance;

        public static renownShopHandler getInstance() {
            if (instance == null) {
                instance = new renownShopHandler();
            }

            return instance;
        }

        private final String inventoryName = "Renown Shop";

        private void createRenownShopItem(Inventory inventory, Document document) {
            ItemStack itemStack = new ItemStack(Material.BEACON);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(yellow + "Renown Shop");

            List<String> lore = new ArrayList<>();
            lore.add(gray + "Use " + yellow + "Renown " + gray + "earned from");
            lore.add(aqua + "Prestige " + gray + "to unlock unique");
            lore.add(gray + "upgrades!");
            lore.add("");
            lore.add(gray.toString() + italic + "These upgrades are safe from");
            lore.add(gray.toString() + italic + "prestige reset.");
            lore.add("");
            lore.add(gray + "Renown: " + yellow + document.getInteger("renown") + " Renown");
            lore.add("");
            lore.add(yellow + "Click to browse!");

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(15, itemStack);
        }

        @EventHandler
        public void inventoryClicked(InventoryClickEvent e) {

        }
    }
}
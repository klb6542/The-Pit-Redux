package me.keegan.pitredux;

import com.mongodb.client.MongoCollection;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import me.keegan.commands.announce;
import me.keegan.enchantments.*;
import me.keegan.global.mongodb;
import me.keegan.global.placeholder;
import me.keegan.items.lame.first_aid_egg;
import me.keegan.items.pickaxes.eternal_pickaxe;
import me.keegan.items.potions.jump_boost;
import me.keegan.lobby.enchantedlake.enchantedLakeHandler;
import me.keegan.events.nightquest.nightQuestHandler;
import me.keegan.global.citizens;
import me.keegan.handlers.*;
import me.keegan.items.hats.kings_helmet;
import me.keegan.items.lame.cherry;
import me.keegan.items.lame.mini_cake;
import me.keegan.items.lame.pants_bundle;
import me.keegan.items.pants.dark_pants;
import me.keegan.items.special.funky_feather;
import me.keegan.items.special.gem;
import me.keegan.items.special.philosophers_cactus;
import me.keegan.items.vile.vile;
import me.keegan.items.vile.vile_block;
import me.keegan.lobby.lobbyHandler;
import me.keegan.mysticwell.mysticWell;
import me.keegan.npcs.*;
import me.keegan.utils.*;
import me.keegan.vanilla.anvilHandler;
import me.keegan.vanilla.itemHandler;
import me.keegan.vanilla.potionHandler;
import org.bson.Document;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static com.comphenix.protocol.ProtocolLibrary.getProtocolManager;

/*
 * Copyright (c) 2024. Created by klb.
 */

public final class ThePitRedux extends JavaPlugin implements Listener {
    private static ThePitRedux plugin;

    public boolean NoteBlockAPI = true;
    public boolean Citizens = true;
    public boolean ProtocolLib = true;

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new playerDamageHandler(), this);
        getServer().getPluginManager().registerEvents(new entityDamageHandler(), this);
        getServer().getPluginManager().registerEvents(new mysticHandler(), this);
        getServer().getPluginManager().registerEvents(new mysticHandler.mysticDrops(), this);
        getServer().getPluginManager().registerEvents(new nightQuestHandler(), this);
        getServer().getPluginManager().registerEvents(new citizens(), this);
        getServer().getPluginManager().registerEvents(new enchantedLakeHandler(), this);
        getServer().getPluginManager().registerEvents(new anvilHandler(), this);
        getServer().getPluginManager().registerEvents(new entityPickUpItemHandler(), this);
        getServer().getPluginManager().registerEvents(new lobbyHandler(), this);
        getServer().getPluginManager().registerEvents(new potionHandler(), this);
        getServer().getPluginManager().registerEvents(new prestige.prestigeHandler(), this);
        getServer().getPluginManager().registerEvents(new prestige.renownShopHandler(), this);
    }

    private void registerEnchants() {
        mysticUtil mysticUtilInstance = me.keegan.utils.mysticUtil.getInstance();

        mysticUtilInstance.registerEnchant(new Guts());
        mysticUtilInstance.registerEnchant(new SpeedyKill());
        mysticUtilInstance.registerEnchant(new Lifesteal());
        mysticUtilInstance.registerEnchant(new Perun());
        mysticUtilInstance.registerEnchant(new MegaLongbow());
        mysticUtilInstance.registerEnchant(new Volley());
        mysticUtilInstance.registerEnchant(new Wasp());
        mysticUtilInstance.registerEnchant(new Solitude());
        mysticUtilInstance.registerEnchant(new Hearts());
        mysticUtilInstance.registerEnchant(new Singularity());
        mysticUtilInstance.registerEnchant(new Sweaty());
        mysticUtilInstance.registerEnchant(new Shark());
        mysticUtilInstance.registerEnchant(new XPReserve());
        mysticUtilInstance.registerEnchant(new Peroxide());
        mysticUtilInstance.registerEnchant(new Sharp());
        mysticUtilInstance.registerEnchant(new PainFocus());
        mysticUtilInstance.registerEnchant(new Crush());
        mysticUtilInstance.registerEnchant(new Parasite());
        mysticUtilInstance.registerEnchant(new SnowmenArmy());
        mysticUtilInstance.registerEnchant(new Explosive());
        mysticUtilInstance.registerEnchant(new PantsRadar());
        mysticUtilInstance.registerEnchant(new PurpleCaffeine());
        mysticUtilInstance.registerEnchant(new Prick());
        mysticUtilInstance.registerEnchant(new XPDuplex());
        mysticUtilInstance.registerEnchant(new GottaGoFast());
        mysticUtilInstance.registerEnchant(new Steaks());
        mysticUtilInstance.registerEnchant(new Billionaire());
        mysticUtilInstance.registerEnchant(new Supine());
        //mysticUtilInstance.registerEnchant(new BooBoo());
        mysticUtilInstance.registerEnchant(new Royalty());
        mysticUtilInstance.registerEnchant(new DevilChicks());
        mysticUtilInstance.registerEnchant(new Chipping());
        mysticUtilInstance.registerEnchant(new Fletching());
        mysticUtilInstance.registerEnchant(new MixedCombat());
        mysticUtilInstance.registerEnchant(new ComboStun());
        mysticUtilInstance.registerEnchant(new Providence());
        mysticUtilInstance.registerEnchant(new WolfPack());
        mysticUtilInstance.registerEnchant(new ComboDamage());
        mysticUtilInstance.registerEnchant(new KingBuster());
        mysticUtilInstance.registerEnchant(new Punisher());
        mysticUtilInstance.registerEnchant(new SprintDrain());
        mysticUtilInstance.registerEnchant(new Protection());
        mysticUtilInstance.registerEnchant(new McSwimmer());
        mysticUtilInstance.registerEnchant(new Grasshopper());
        mysticUtilInstance.registerEnchant(new Sniper());
        mysticUtilInstance.registerEnchant(new BottomlessQuiver());
        mysticUtilInstance.registerEnchant(new Gamble());
        mysticUtilInstance.registerEnchant(new Calidum());

        // aqua
        mysticUtilInstance.registerEnchant(new Inspire());
        mysticUtilInstance.registerEnchant(new AquaticGuard());
        mysticUtilInstance.registerEnchant(new Stereo());
        mysticUtilInstance.registerEnchant(new GuardiansAura());
        mysticUtilInstance.registerEnchant(new TheRod());
    }

    private void registerItems() {
        itemUtil.registerItem(new mysticWell());
        itemUtil.registerItem(new vile());
        itemUtil.registerItem(new vile_block());
        itemUtil.registerItem(new dark_pants());
        itemUtil.registerItem(new gem());
        itemUtil.registerItem(new philosophers_cactus());
        itemUtil.registerItem(new kings_helmet());
        itemUtil.registerItem(new mini_cake());
        itemUtil.registerItem(new cherry());
        itemUtil.registerItem(new pants_bundle());
        itemUtil.registerItem(new funky_feather());
        itemUtil.registerItem(new jump_boost());
        itemUtil.registerItem(new first_aid_egg());
        itemUtil.registerItem(new eternal_pickaxe());
    }

    private void registerNPCs() {
        npcUtil.registerNPC(new king());
        npcUtil.registerNPC(new itemshop());
        npcUtil.registerNPC(new quests());
        npcUtil.registerNPC(new upgrades());
        npcUtil.registerNPC(new prestige());
        npcUtil.registerNPC(new fisherman());
        npcUtil.registerNPC(new miner());
    }

    private void registerCommands() {
        getCommand("hello").setExecutor(new mysticUtil());
        getCommand("announce").setExecutor(new announce());
    }

    private void registerPacketListeners() {
        protocolUtil.registerPackets(new first_aid_egg());
        protocolUtil.registerPackets(new lobbyHandler());
    }

    private void registerPlaceholders() {
        new placeholder().register();
    }

    private void registerDependencies() {
        if (!this.getServer().getPluginManager().isPluginEnabled("NoteBlockAPI")){
            getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
            NoteBlockAPI = false;
        }

        if (!this.getServer().getPluginManager().isPluginEnabled("Citizens")){
            getLogger().severe("*** Citizens is not installed or not enabled. ***");
            Citizens = false;
        }

        if (!this.getServer().getPluginManager().isPluginEnabled("ProtocolLib")){
            getLogger().severe("*** ProtocolLib is not installed or not enabled. ***");
            ProtocolLib = false;
        }
    }

    private void startup() {
        // makes sure everything is loaded
        new BukkitRunnable() {

            @Override
            public void run() {
                setupUtils.pluginEnabled(new Hearts());
                setupUtils.pluginEnabled(new SnowmenArmy());
                setupUtils.pluginEnabled(new nightQuestHandler());
                setupUtils.pluginEnabled(new GottaGoFast());
                setupUtils.pluginEnabled(new Supine());
                setupUtils.pluginEnabled(new BooBoo());
                setupUtils.pluginEnabled(new DevilChicks());
                setupUtils.pluginEnabled(new WolfPack());
                setupUtils.pluginEnabled(new itemHandler());
            }

        }.runTaskLater(this, 2);
    }

    private void shutdown() {
        // this gets fired on server reload as well as server shutdown

        setupUtils.pluginDisabled(new Hearts());
        setupUtils.pluginDisabled(new SnowmenArmy());
        setupUtils.pluginDisabled(new nightQuestHandler());
        setupUtils.pluginDisabled(new GottaGoFast());
        setupUtils.pluginDisabled(new Supine());
        setupUtils.pluginDisabled(new BooBoo());
        setupUtils.pluginDisabled(new DevilChicks());
        setupUtils.pluginDisabled(new WolfPack());
        setupUtils.pluginDisabled(new itemHandler());
    }

    @Override
    public void onEnable() {
        plugin = this;

        mongodb.getInstance().startDatabaseConnection();
        registerEnchants(); // also registers the event listeners
        registerItems(); // also registers the event listeners
        registerListeners();
        registerCommands();
        registerDependencies();
        registerPacketListeners();
        registerPlaceholders();
        registerNPCs();

        startup();
    }

    @Override
    public void onDisable() {
        mongodb.getInstance().closeDatabaseConnection();
        getProtocolManager().removePacketListeners(this);
        shutdown();
    }

    // auto updater for documents in the survival collection
    @EventHandler
    public void playerJoined(PlayerJoinEvent e) {
        MongoCollection<Document> collection = mongodb.getInstance().getSurvivalCollection();
        if (collection == null) { return; }

        Document defaultPlayerDocument = mongodb.getInstance().createDefaultPlayerDocument(e.getPlayer());
        Document playerDocument = mongodb.getInstance().getDocumentFromCollection(collection, defaultPlayerDocument);

        if (playerDocument == null) {
            collection.insertOne(defaultPlayerDocument);
        }else{
            for (String key : defaultPlayerDocument.keySet()) {
                if (playerDocument.containsKey(key)) { continue; }

                playerDocument.append(key, defaultPlayerDocument.get(key));
            }

            mongodb.getInstance().replaceDocumentFromCollection(collection, playerDocument);
        }
    }

    public static ThePitRedux getPlugin() {
        return plugin;
    }
}
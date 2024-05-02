package me.keegan.pitredux;

import me.keegan.enchantments.*;
import me.keegan.events.nightquest.nightQuestHandler;
import me.keegan.handlers.entityDamageHandler;
import me.keegan.handlers.mysticHandler;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.items.pants.dark_pants;
import me.keegan.items.special.gem;
import me.keegan.items.special.philosophers_cactus;
import me.keegan.items.vile.vile;
import me.keegan.items.vile.vile_block;
import me.keegan.mysticwell.mysticWell;
import me.keegan.utils.itemUtil;
import me.keegan.utils.mysticUtil;
import me.keegan.utils.setupUtils;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Copyright (c) 2024. Created by klb.
 */

public final class ThePitRedux extends JavaPlugin {
    private static ThePitRedux plugin;
    private boolean NoteBlockAPI = true;

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new playerDamageHandler(), this);
        getServer().getPluginManager().registerEvents(new entityDamageHandler(), this);
        getServer().getPluginManager().registerEvents(new mysticHandler(), this);
        getServer().getPluginManager().registerEvents(new mysticHandler.mysticDrops(), this);
        getServer().getPluginManager().registerEvents(new nightQuestHandler(), this);
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
    }

    private void registerItems() {
        itemUtil.registerItem(new mysticWell());
        itemUtil.registerItem(new vile());
        itemUtil.registerItem(new vile_block());
        itemUtil.registerItem(new dark_pants());
        itemUtil.registerItem(new gem());
        itemUtil.registerItem(new philosophers_cactus());
    }

    private void registerCommands() {
        getCommand("hello").setExecutor(new mysticUtil());
    }

    private void registerDependencies() {
        if (!this.getServer().getPluginManager().isPluginEnabled("NoteBlockAPI")){
            getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
            NoteBlockAPI = false;
        }
    }

    private void startup() {
        setupUtils.pluginEnabled(new Stereo());
        setupUtils.pluginEnabled(new Hearts());
        setupUtils.pluginEnabled(new SnowmenArmy());
        setupUtils.pluginEnabled(new nightQuestHandler());
        setupUtils.pluginEnabled(new GottaGoFast());
        setupUtils.pluginEnabled(new Supine());
    }

    private void shutdown() {
        // this gets fired on server reload as well as server shutdown

        setupUtils.pluginDisabled(new Stereo());
        setupUtils.pluginDisabled(new Hearts());
        setupUtils.pluginDisabled(new SnowmenArmy());
        setupUtils.pluginDisabled(new nightQuestHandler());
        setupUtils.pluginDisabled(new GottaGoFast());
        setupUtils.pluginDisabled(new Supine());
    }

    @Override
    public void onEnable() {
        plugin = this;

        registerEnchants(); // also registers the event listeners
        registerItems(); // also registers the event listeners
        registerListeners();
        registerCommands();
        registerDependencies();

        startup();
    }

    @Override
    public void onDisable() {
        shutdown();
    }

    public static ThePitRedux getPlugin() {
        return plugin;
    }
}

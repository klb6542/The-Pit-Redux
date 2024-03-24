package me.keegan.pitredux;

import me.keegan.enchantments.*;
import me.keegan.handlers.entityDamageHandler;
import me.keegan.handlers.mysticHandler;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.mysticUtil;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Copyright (c) 2024. Created by klb.
 */

public final class ThePitRedux extends JavaPlugin {
    private static ThePitRedux plugin;

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new playerDamageHandler(), this);
        getServer().getPluginManager().registerEvents(new entityDamageHandler(), this);
        getServer().getPluginManager().registerEvents(new mysticHandler(), this);
    }

    private void registerEnchants() {
        mysticUtil mysticUtilInstance = me.keegan.utils.mysticUtil.getInstance();

        mysticUtilInstance.registerEnchant(new Guts());
        mysticUtilInstance.registerEnchant(new SpeedyKill());
        mysticUtilInstance.registerEnchant(new Lifesteal());
        mysticUtilInstance.registerEnchant(new Perun());
        mysticUtilInstance.registerEnchant(new MegaLongbow());
    }

    private void registerCommands() {
        getCommand("hello").setExecutor(new mysticUtil());
    }

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("Hello, world");

        registerEnchants(); // also registers the event listeners
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ThePitRedux getPlugin() {
        return plugin;
    }
}

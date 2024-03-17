package me.keegan.pitredux;

import me.keegan.handlers.entityDamageHandler;
import me.keegan.handlers.playerDamageHandler;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Copyright (c) 2024. Created by klb.
 */

public final class ThePitRedux extends JavaPlugin {
    private static ThePitRedux plugin;

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new playerDamageHandler(), this);
        getServer().getPluginManager().registerEvents(new entityDamageHandler(), this);
    }

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("Hello, world");

        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ThePitRedux getPlugin() {
        return plugin;
    }
}

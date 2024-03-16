package me.keegan.pitredux;

import org.bukkit.plugin.java.JavaPlugin;

/*
 * Copyright (c) 2024. Created by klb.
 */

public final class ThePitRedux extends JavaPlugin {
    private static ThePitRedux plugin;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("Hello, world");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ThePitRedux getPlugin() {
        return plugin;
    }
}

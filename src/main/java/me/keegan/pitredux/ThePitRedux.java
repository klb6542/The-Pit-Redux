package me.keegan.pitredux;

import me.keegan.enchantments.Guts;
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

    private void registerEnchantments() {
        getServer().getPluginManager().registerEvents(new Guts(), this);
    }

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("Hello, world");

        registerEnchantments();
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

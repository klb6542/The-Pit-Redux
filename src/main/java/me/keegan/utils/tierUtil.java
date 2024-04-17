package me.keegan.utils;

import me.keegan.classes.Tier;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import static me.keegan.utils.formatUtil.*;

public interface tierUtil {
    Tier<ChatColor> normalColorTiers = new Tier<>(
            green,
            yellow,
            red
    );

    Tier<Material> normalPaneColorTiers = new Tier<>(
            Material.LIME_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE
    );

    Tier<Material> darkPaneColorTiers = new Tier<>(
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.GREEN_STAINED_GLASS_PANE,
            Material.GREEN_STAINED_GLASS_PANE
    );

    @Nullable ChatColor getColorFromTier(int tier); // tier 0 = fresh
    @Nullable Material getColorPaneFromTier(int tier); // tier 0 = fresh
}

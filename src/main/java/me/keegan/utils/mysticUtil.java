package me.keegan.utils;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class mysticUtil implements CommandExecutor {
    private final List<enchantUtil> enchants = new ArrayList<>();

    public static mysticUtil getInstance() {
        return new mysticUtil();
    }

    private @Nullable List<String> getItemLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) { return null; }

        return (!itemMeta.hasLore()) ? null : itemMeta.getLore();
    }

    public void registerEnchant(enchantUtil enchant) {
        ThePitRedux.getPlugin().getServer().getPluginManager().registerEvents(enchant, ThePitRedux.getPlugin()); // register listener

        this.enchants.add(enchant); // register enchant to list
    }

    public List<enchantUtil> getEnchants() {
        return this.enchants;
    }

    public Boolean hasEnchant(ItemStack itemStack, String enchantName) {
        List<String> lore = this.getItemLore(itemStack);

        return lore != null && lore.stream().anyMatch(s -> s.contains(enchantName));
    }

    public void addEnchant(ItemStack itemStack, String enchantName, Integer enchantLevel) {

    }

    public void removeEnchant(ItemStack itemStack, String enchantName) {

    }

    public Integer getEnchantLevel() {
        return 0;
    }

    public void addLives(ItemStack itemStack, Integer lives) {

    }

    public void addMaxLives(ItemStack itemStack, Integer maxLives) {

    }

    public void removeLives(ItemStack itemStack, Integer lives) {

    }

    public void removeMaxLives(ItemStack itemStack, Integer maxLives) {

    }

    public Integer getLives(ItemStack itemStack) {
        return 0;
    }

    public Integer getMaxLives(ItemStack itemStack) {
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        ThePitRedux.getPlugin().getLogger().info("YOO S");

        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;

        List<String> lore = new ArrayList<>();
        lore.add("Hello, world!");
        lore.add("This is some");
        lore.add(ChatColor.GREEN + "Cool");
        lore.add("lore!");

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        ThePitRedux.getPlugin().getServer().getPlayer("qsmh").getInventory().addItem(itemStack);

        List<String> theLore = this.getItemLore(itemStack);

        return true;
    }
}

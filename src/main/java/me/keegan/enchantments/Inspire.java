package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.wordUtil.integerToMultiplier;

public class Inspire extends enchantUtil {
    private final Integer dropMultiplier = 2;

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.LEATHER_LEGGINGS};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.AQUA;
    }

    @Override
    public String getName() {
        return "Inspire";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Receive {1}{2}{0} fishing drops",
                        gray, aqua, integerToMultiplier(dropMultiplier))
        };
    }

    @Override
    public Integer getMaxLevel() {
        return this.getEnchantDescription().length;
    }

    @Override
    public boolean isRareEnchant() {
        return false;
    }

    @Override
    public boolean isMysticWellEnchant() {
        return true;
    }

    @Override
    public void executeEnchant(Object[] args) {
        PlayerFishEvent e = (PlayerFishEvent) args[0];
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH || e.getCaught() == null) { return; }

        Item item = (Item) e.getCaught();
        ItemStack itemStack = item.getItemStack();

        itemStack.setAmount(itemStack.getAmount() * 2);
    }

    @EventHandler
    public void playerFished(PlayerFishEvent e) {
        Object[] args = new Object[]{
                e,
                (e.getPlayer().getEquipment() != null) ? e.getPlayer().getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(e.getPlayer(), args);
    }
}

package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.*;

public class BottomlessQuiver extends enchantUtil {
    private final Integer[] arrowsPerLevel = new Integer[]{1, 2, 3};

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.BOW};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.NORMAL;
    }

    @Override
    public String getName() {
        return "Bottomless Quiver";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Get {1}{2} arrows{0} on arrow hit", gray, white, arrowsPerLevel[0]),
                MessageFormat.format("{0}Get {1}{2} arrows{0} on arrow hit", gray, white, arrowsPerLevel[1]),
                MessageFormat.format("{0}Get {1}{2} arrows{0} on arrow hit", gray, white, arrowsPerLevel[2]),
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
        int enchantLevel = (int) args[2];

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        Player shooter = (Player) ((Arrow) e.getDamager()).getShooter();
        if (shooter.getInventory().firstEmpty() == -1) { return; }

        ItemStack arrow = new ItemStack(Material.ARROW);
        arrow.setAmount(arrowsPerLevel[enchantLevel]);

        shooter.getInventory().addItem(arrow);
    }

    @EventHandler
    public void arrowDamaged(EntityDamageByEntityEvent e) {
        if (!entityUtil.damagerIsArrow(e)) { return; }

        Arrow arrow = (Arrow) e.getDamager();
        LivingEntity shooter = (LivingEntity) arrow.getShooter();
        if (!(shooter instanceof Player)) { return; }

        Object[] args = new Object[]{
                e,
                (shooter.getEquipment() != null && shooter.getEquipment().getItemInMainHand().getType() == Material.BOW)
                        ? shooter.getEquipment().getItemInMainHand()
                        : shooter.getEquipment().getItemInOffHand(),
                this
        };

        this.attemptEnchantExecution(shooter, args);
    }
}

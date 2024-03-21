package me.keegan.enchantments;

import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.gray;
import static me.keegan.utils.formatUtil.red;

public class Lifesteal extends enchantUtil {
    private final Integer[] healingPercentPerLevel = new Integer[]{4, 8, 13};
    private final double maxHeartsToHeal = 1.5; // in hearts not hp

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.GOLDEN_SWORD};
    }

    @Override
    public String getName() {
        return "Lifesteal";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Heal for {1}{2}% {0}of damage dealt up/n" +
                        "{0}to {1}{3}❤", gray, red, healingPercentPerLevel[0], maxHeartsToHeal),

                MessageFormat.format("{0}Heal for {1}{2}% {0}of damage dealt up/n" +
                        "{0}to {1}{3}❤", gray, red, healingPercentPerLevel[1], maxHeartsToHeal),

                MessageFormat.format("{0}Heal for {1}{2}% {0}of damage dealt up/n" +
                        "{0}to {1}{3}❤", gray, red, healingPercentPerLevel[2], maxHeartsToHeal),
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
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        LivingEntity damager = (LivingEntity) e.getDamager();

        this.heal(damager,
                Math.max(0.0,
                Math.min(maxHeartsToHeal * 2,
                        (healingPercentPerLevel[enchantLevel] / 100.0) * playerDamageHandler.getInstance().calculateNewDamage(e, e.getFinalDamage()))));
    }

    // executes after all damage has been put into playerDamageHandler
    // but before playerDamageHandler @EventHandler executes
    @EventHandler(priority = EventPriority.HIGH)
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity) || !(e.getDamager() instanceof Player)) { return; }

        Player damager = (Player) e.getDamager();

        Object[] args = new Object[]{
                e,
                damager.getInventory().getItemInMainHand(),
                this
        };

        this.attemptEnchantExecution(args);
    }
}

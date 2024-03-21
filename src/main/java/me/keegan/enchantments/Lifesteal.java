package me.keegan.enchantments;

import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.gray;
import static me.keegan.utils.formatUtil.red;

public class Lifesteal extends enchantUtil {
    private final Integer[] healingPercentPerLevel = new Integer[]{4, 8, 13};

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
                MessageFormat.format("{0}Heal for {1}{2}% {0}of damage dealt up\n" +
                        "{0}to {1}1.5❤", gray, red, healingPercentPerLevel[0]),

                MessageFormat.format("{0}Heal for {1}{2}% {0}of damage dealt up\n" +
                        "{0}to {1}1.5❤", gray, red, healingPercentPerLevel[1]),

                MessageFormat.format("{0}Heal for {1}{2}% {0}of damage dealt up\n" +
                        "{0}to {1}1.5❤", gray, red, healingPercentPerLevel[2]),
        };
    }

    @Override
    public Integer getMaxLevel() {
        return this.getEnchantMaterial().length;
    }

    @Override
    public boolean isRareEnchant() {
        return false;
    }

    @Override
    public void executeEnchant(Object... args) {

    }

    // executes after all damage has been put into playerDamageHandler
    // but before playerDamageHandler @EventHandler executes
    @EventHandler(priority = EventPriority.HIGH)
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity) || !(e.getDamager() instanceof LivingEntity)) { return; }

        LivingEntity damaged = (LivingEntity) e.getEntity();
        if (damaged.getKiller() == null) { return; }

        Object[] args = new Object[]{
                e,
                damaged.getKiller().getInventory().getItemInMainHand(),
                this
        };

        this.attemptEnchantExecution(args);
    }
}

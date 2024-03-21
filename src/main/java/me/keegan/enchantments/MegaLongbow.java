package me.keegan.enchantments;

import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.text.MessageFormat;

import static me.keegan.utils.formatUtil.gray;
import static me.keegan.utils.formatUtil.green;
import static me.keegan.utils.romanUtil.integerToRoman;

public class MegaLongbow extends enchantUtil {
    private final Integer[] jumpBoostAmplifierPerLevel = new Integer[]{1, 2, 3};
    private final int jumpBoostDuration = 2;

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.BOW};
    }

    @Override
    public String getName() {
        return "Mega Longbow";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}One shot per second, this bow is/n" +
                        "{0}automatically fully drawn and/n" +
                        "{0}grants {1}Jump Boost {2} ({3}s)", gray, green, integerToRoman(jumpBoostAmplifierPerLevel[0], false), jumpBoostDuration),

                MessageFormat.format("{0}One shot per second, this bow is/n" +
                        "{0}automatically fully drawn and/n" +
                        "{0}grants {1}Jump Boost {2} ({3}s)", gray, green, integerToRoman(jumpBoostAmplifierPerLevel[1], false), jumpBoostDuration),

                MessageFormat.format("{0}One shot per second, this bow is/n" +
                        "{0}automatically fully drawn and/n" +
                        "{0}grants {1}Jump Boost {2} ({3}s)", gray, green, integerToRoman(jumpBoostAmplifierPerLevel[2], false), jumpBoostDuration),
        };
    }

    @Override
    public Integer getMaxLevel() {
        return this.getEnchantDescription().length;
    }

    @Override
    public boolean isRareEnchant() {
        return true;
    }

    @Override
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];

        EntityShootBowEvent e = (EntityShootBowEvent) args[0];
        LivingEntity shooter = e.getEntity();
        Arrow arrow = (Arrow) e.getProjectile();

        arrow.setCritical(true);
        arrow.setVelocity(shooter.getLocation().getDirection().multiply(2.90));

        this.setJumpBoost(shooter, jumpBoostAmplifierPerLevel[enchantLevel], jumpBoostDuration);
    }

    @EventHandler
    public void bowShot(EntityShootBowEvent e) {
        if (!(e.getProjectile() instanceof Arrow)) { return; }

        Object[] args = new Object[]{
                e,
                e.getBow(),
                this
        };

        this.attemptEnchantExecution(args);
    }
}

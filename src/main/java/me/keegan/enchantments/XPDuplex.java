package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;
import java.util.Random;

import static me.keegan.utils.formatUtil.*;

public class XPDuplex extends enchantUtil {
    private final Integer[] chancePerLevel = new Integer[]{35, 50, 65};
    private final Integer[] multiplierAmountPerLevel = new Integer[]{2, 3, 3};

    private void playMultiplierSound(LivingEntity livingEntity) {
        new BukkitRunnable(){
            final World world = livingEntity.getWorld();
            final Location location = livingEntity.getLocation();

            int count = 0;
            float pitch = 2f;

            @Override
            public void run() {
                switch (count) {
                    case 0:
                        world.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 3f, pitch);
                        break;
                    case 1:
                        pitch = 1f;
                        world.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 3f, pitch);
                        break;
                    case 2:
                        pitch = 5.5f;
                        world.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 3f, pitch);
                        this.cancel();
                }

                count++;
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, 3);
    }

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.LEATHER_LEGGINGS};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.NORMAL;
    }

    @Override
    public String getName() {
        return "XP Duplex";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Killing entities has a {1}{2}% chance{0} to/n"
                        + "{0}drop {1}+{3}% XP",
                        gray, aqua, chancePerLevel[0], multiplierAmountPerLevel[0] * 100),

                MessageFormat.format("{0}Killing entities has a {1}{2}% chance{0} to/n"
                                + "{0}drop {1}+{3}% XP",
                        gray, aqua, chancePerLevel[1], multiplierAmountPerLevel[1] * 100),

                MessageFormat.format("{0}Killing entities has a {1}{2}% chance{0} to/n"
                                + "{0}drop {1}+{3}% XP",
                        gray, aqua, chancePerLevel[2], multiplierAmountPerLevel[2] * 100)
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
    public boolean isMysticWellEnchant() {
        return true;
    }

    @Override
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];

        EntityDeathEvent e = (EntityDeathEvent) args[0];
        LivingEntity killer = e.getEntity().getKiller();
        if (new Random().nextInt(100) > chancePerLevel[enchantLevel] - 1) { return; }

        playMultiplierSound(killer);
        e.setDroppedExp(e.getDroppedExp() * multiplierAmountPerLevel[enchantLevel]);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDied(EntityDeathEvent e) {
        LivingEntity killer = e.getEntity().getKiller();
        if (killer == null || killer.getEquipment() == null) { return; }

        Object[] args = new Object[]{
                e,
                (killer.getEquipment().getLeggings() != null) ? killer.getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(args);
    }
}

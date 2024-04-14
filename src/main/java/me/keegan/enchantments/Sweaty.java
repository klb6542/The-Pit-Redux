package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Random;

import static me.keegan.utils.formatUtil.*;

public class Sweaty extends enchantUtil {
    private final Integer[] expPointsPerLevel = new Integer[]{2, 3, 4};
    private final Integer[] levelUpChancePerLevel = new Integer[]{0, 1, 1};

    private static final HashMap<EntityDeathEvent, Integer> xp = new HashMap<>();

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{
                Material.LEATHER_LEGGINGS,
                Material.GOLDEN_SWORD,
                Material.BOW
        };
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.NORMAL;
    }

    @Override
    public String getName() {
        return "Sweaty";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Earn {1}+{2} XP points{0} from kills/n",
                        gray, aqua, expPointsPerLevel[0]),

                MessageFormat.format("{0}Earn {1}+{2} XP points{0} from kills with a/n" +
                                "{1}{3}% chance{0} to level up",
                        gray, aqua, expPointsPerLevel[1], levelUpChancePerLevel[1]),

                MessageFormat.format("{0}Earn {1}+{2} XP points{0} from kills with a/n" +
                                "{1}{3}% chance{0} to level up",
                        gray, aqua, expPointsPerLevel[2], levelUpChancePerLevel[2]),
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
        EntityDeathEvent e = (EntityDeathEvent) args[0];

        // put xp in hashmap to calculate later
        xp.put(e, xp.getOrDefault(e, 0) + expPointsPerLevel[enchantLevel]);

        if (enchantLevel < 2
                || new Random().nextInt(100)
                > levelUpChancePerLevel[enchantLevel]) { return; }

        // volume, pitch
        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        e.getEntity().getKiller().giveExpLevels(1);
    }

    // 1st entityDied event puts xp in hashmap
    @EventHandler
    public void entityDied1(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) { return; }

        LivingEntity killer = e.getEntity().getKiller();
        EntityEquipment equipment = killer.getEquipment();
        if (equipment == null) { return; }

        // leggings check
        if (equipment.getLeggings() != null) {
            Object[] args = new Object[]{
                    e,
                    killer.getEquipment().getLeggings(),
                    this
            };

            this.attemptEnchantExecution(args);
        }

        // sword check
        if (equipment.getItemInMainHand().getType() == Material.GOLDEN_SWORD) {
            Object[] args = new Object[]{
                    e,
                    equipment.getItemInMainHand(),
                    this
            };

            this.attemptEnchantExecution(args);
        }

        // bow check
        if (equipment.getItemInMainHand().getType() == Material.BOW
                || equipment.getItemInOffHand().getType() == Material.BOW) {
            Object[] args = new Object[]{
                    e,
                    (equipment.getItemInMainHand().getType() == Material.BOW)
                            ? equipment.getItemInMainHand()
                            : equipment.getItemInOffHand(),
                    this
            };

            this.attemptEnchantExecution(args);
        }
    }

    // executes after 1st entityDied event puts xp in hashmap
    @EventHandler(priority = EventPriority.HIGH)
    public void entityDied2(EntityDeathEvent e) {
        if (!xp.containsKey(e)) { return; }

        e.setDroppedExp(e.getDroppedExp() + xp.getOrDefault(e, 0));
        xp.remove(e);
    }
}

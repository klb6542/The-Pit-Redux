package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.UUID;

import static me.keegan.utils.formatUtil.gray;
import static me.keegan.utils.formatUtil.white;

/*
 * Copyright (c) 2024. Created by klb.
 */


public class Volley extends enchantUtil {
    private final Integer[] arrowsPerLevel = new Integer[]{3, 4, 5};
    private static final HashMap<UUID, Integer> arrowCounter = new HashMap<>();
    private static final HashMap<UUID, Object[]> runnables = new HashMap<>();

    /*
     * Object[0] = speed of arrow (double)
     * Object[1] = arrow is critical (boolean)
     */

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
        return "Volley";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Shoot {1}{2} arrows{0} at once", gray, white, arrowsPerLevel[0]),
                MessageFormat.format("{0}Shoot {1}{2} arrows{0} at once", gray, white, arrowsPerLevel[1]),
                MessageFormat.format("{0}Shoot {1}{2} arrows{0} at once", gray, white, arrowsPerLevel[2])
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
        EntityShootBowEvent e = (EntityShootBowEvent) args[0];
        Arrow arrow = (Arrow) e.getProjectile();

        LivingEntity shooter = e.getEntity();
        UUID uuid = shooter.getUniqueId();

        Integer arrowsToShoot = arrowsPerLevel[(int) args[2]];

        // https://www.spigotmc.org/threads/spawn-an-arrow-and-throw-it-straight.277723/
        Arrow volleyArrow = shooter.launchProjectile(Arrow.class);
        volleyArrow.setTicksLived(1);

        volleyArrow.setCritical((boolean)
                runnables.getOrDefault(shooter.getUniqueId(), new Object[]{
                0.0,
                arrow.isCritical()
        })[1]);

        volleyArrow.setVelocity(shooter.getEyeLocation().getDirection().multiply((double)
                runnables.getOrDefault(shooter.getUniqueId(), new Object[]{
                arrow.getVelocity().length(),
                false
        })[0]));

        if (runnables.containsKey(uuid)) { return; }

        runnables.put(uuid, new Object[]{
                arrow.getVelocity().length(),
                arrow.isCritical()
        });

        new BukkitRunnable() {

            @Override
            public void run() {
                if (arrowCounter.containsKey(uuid) && arrowCounter.getOrDefault(uuid, 0) >= arrowsToShoot - 1) {
                    runnables.remove(uuid);
                    arrowCounter.remove(uuid);

                    this.cancel();
                    return;
                }

                arrowCounter.put(uuid, arrowCounter.getOrDefault(uuid, 0) + 1);

                // this event does not create the arrow
                EntityShootBowEvent volleyEvent = new EntityShootBowEvent(
                        shooter,
                        (ItemStack) args[1],
                        e.getConsumable(),
                        e.getProjectile(),
                        e.getHand(),
                        e.getForce(),
                        false

                );

                // proc other enchantments
                ThePitRedux.getPlugin().getServer().getPluginManager().callEvent(volleyEvent);
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 2, 2);
    }

    // executes after Mega longBow enchant
    @EventHandler(priority = EventPriority.HIGH)
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

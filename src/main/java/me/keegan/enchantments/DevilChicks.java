package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import me.keegan.utils.setupUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.MessageFormat;
import java.util.*;

import static me.keegan.utils.formatUtil.*;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class DevilChicks extends enchantUtil implements setupUtils {
    private static final HashMap<UUID, List<Chicken>> playerChickens = new HashMap<>();
    private static final HashMap<UUID, BukkitTask> runnables = new HashMap<>();

    // per arrow
    private final Integer[] chickensToSpawnPerLevel = new Integer[]{1, 2, 3};
    private final Double[] trueDamagePerLevel = new Double[]{2.0, 2.5, 2.7};
    private final Double[] blockRadiusPerLevel = new Double[]{0.8, 1.2, 1.5};
    private final String chickenName = red + "DEVIL!";

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
        return "Devil Chicks!";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Arrows spawn an explosive chicken", gray),

                MessageFormat.format("{0}Arrows spawn many explosive/n"
                        + "{0}chickens", gray),

                MessageFormat.format("{0}Arrows spawn too many explosive/n"
                        + "{0}chickens", gray),
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

    private void playSound(Location location, World world) {
        final int[] count = new int[]{1};
        final float[] pitch = new float[]{0.6f};

        new BukkitRunnable() {

            @Override
            public void run() {
                if (count[0] < 10) {
                    world.playSound(location, Sound.BLOCK_NOTE_BLOCK_SNARE, 1f, pitch[0]);

                    count[0]++;
                    pitch[0] += 0.1f;
                    return;
                }

                world.playSound(location, Sound.ENTITY_CHICKEN_HURT, 1f, 2f);
                this.cancel();
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 0, 1);
    }

    @Override
    public void executeEnchant(Object[] args) {
        int enchantLevel = (int) args[2];
        double radius = blockRadiusPerLevel[enchantLevel];

        ProjectileHitEvent e = (ProjectileHitEvent) args[0];

        Arrow arrow = (Arrow) e.getEntity();
        World world = arrow.getWorld();
        Location arrowLocation = arrow.getLocation();

        LivingEntity shooter = (LivingEntity) arrow.getShooter();
        UUID uuid = shooter.getUniqueId();

        for (int i = 0; i < chickensToSpawnPerLevel[enchantLevel]; i++) {

            Location location = new Location(
                    world,
                    arrowLocation.getX() + (Math.random() - Math.random()) * radius,
                    arrowLocation.getY() + (Math.abs(Math.random() - Math.random())) * radius,
                    arrowLocation.getZ() + (Math.random() - Math.random()) * radius
            );

            Chicken chicken = (Chicken) world.spawnEntity(location, EntityType.CHICKEN);

            chicken.setCustomName(chickenName);
            chicken.setBaby();

            this.addPotionEffect(chicken, PotionEffectType.DAMAGE_RESISTANCE, 9, 60.0);

            List<Chicken> chickens = playerChickens.getOrDefault(uuid, new ArrayList<>());
            chickens.add(chicken);

            playerChickens.put(uuid, chickens);
        }

        playSound(arrowLocation, world);

        runnables.put(uuid, new BukkitRunnable() {

            @Override
            public void run() {
                if (playerChickens.get(uuid).isEmpty()) { this.cancel(); return; }
                Chicken chicken = playerChickens.get(uuid).get(0);
                Location chickenLocation = chicken.getLocation();

                for (Entity entity : chicken.getNearbyEntities(radius, radius, radius)) {
                    if (!(entity instanceof LivingEntity)
                            || entity == shooter
                            || (entity.getCustomName() != null && entity.getCustomName().equals(chickenName))) { continue; }
                    LivingEntity livingEntity = (LivingEntity) entity;

                    playerDamageHandler.getInstance().doTrueDamage(livingEntity, shooter, trueDamagePerLevel[enchantLevel]);
                    createExplosionEffect(livingEntity, chickenLocation);
                }

                world.playSound(arrowLocation, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.6f);
                world.spawnParticle(Particle.EXPLOSION_LARGE, chickenLocation, 1);

                playerChickens.get(uuid).get(0).remove();
                playerChickens.get(uuid).remove(0);
            }

        }.runTaskTimer(ThePitRedux.getPlugin(), 11, 1));
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent e) {
        if (!entityUtil.projectileIsArrow(e)) { return; }
        LivingEntity shooter = (LivingEntity) e.getEntity().getShooter();

        Object[] args = new Object[]{
                e,
                (shooter.getEquipment() != null && shooter.getEquipment().getItemInMainHand().getType() == Material.BOW)
                        ? shooter.getEquipment().getItemInMainHand()
                        : shooter.getEquipment().getItemInOffHand(),
                this
        };

        this.attemptEnchantExecution(shooter, args);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        for (Player player : ThePitRedux.getPlugin().getServer().getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();

            if (runnables.containsKey(uuid)) {
                runnables.get(uuid).cancel();
                runnables.remove(uuid);
            }

            if (playerChickens.containsKey(uuid)) {
                playerChickens.get(uuid).forEach(Chicken::remove);
                playerChickens.remove(uuid);
            }
        }
    }
}

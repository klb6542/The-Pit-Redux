package me.keegan.enchantments;

import me.keegan.enums.cooldownEnums;
import me.keegan.enums.mysticEnums;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;

public class Explosive extends enchantUtil {
    /*
     * first index = y force, higher the number = higher the entity goes up
     * second index = multiplier force, higher number = more the entity moves
     */

    private final List<Double[]> explosionForcesPerLevel = new ArrayList<Double[]>(){{
        add(new Double[]{0.5, 1.10});
        add(new Double[]{0.75, 1.17});
        add(new Double[]{0.95, 1.25});
    }};

    private final Particle[] explosionParticlesPerLevel = new Particle[]{Particle.EXPLOSION_LARGE, Particle.EXPLOSION_HUGE, Particle.EXPLOSION_HUGE};
    private final Integer[] explosionParticleAmountPerLevel = new Integer[]{1, 1, 2};
    private final Integer[] explosionDamagePerLevel = new Integer[]{2, 3, 4};
    private final Integer[] explosionRangePerLevel = new Integer[]{1, 3, 6};

    private final Integer[] cooldownSecondsPerLevel = new Integer[]{5, 3, 5};
    private final String[] onomatopoeiasPerLevel = new String[]{"POP", "BANG", "BOOM"};

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
        return "Explosive";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Arrows go {1}! ({2}s cooldown)",
                        gray, onomatopoeiasPerLevel[0], cooldownSecondsPerLevel[0]),

                MessageFormat.format("{0}Arrows go {1}! ({2}s cooldown)",
                        gray, onomatopoeiasPerLevel[1], cooldownSecondsPerLevel[1]),

                MessageFormat.format("{0}Arrows go {1}! ({2}s cooldown)",
                        gray, onomatopoeiasPerLevel[2], cooldownSecondsPerLevel[2])
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

        ProjectileHitEvent e = (ProjectileHitEvent) args[0];
        Arrow arrow = (Arrow) e.getEntity();
        LivingEntity shooter = (LivingEntity) arrow.getShooter();

        if (this.isOnCooldown(shooter.getUniqueId())) { return; }
        this.addCooldown(shooter.getUniqueId(), cooldownSecondsPerLevel[enchantLevel].longValue(), cooldownEnums.NORMAL);

        Integer explosionRange = explosionRangePerLevel[enchantLevel];

        for (Entity entity : arrow.getNearbyEntities(explosionRange, explosionRange, explosionRange)) {
            if (!(entity instanceof LivingEntity) || entity.equals(shooter)) { continue; }
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.damage(explosionDamagePerLevel[enchantLevel], shooter);

            Vector force = livingEntity.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize().multiply(explosionForcesPerLevel.get(enchantLevel)[1]);
            force.setY(explosionForcesPerLevel.get(enchantLevel)[0]);

            livingEntity.setVelocity(force);
        }

        arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.4f);
        arrow.getWorld().spawnParticle(explosionParticlesPerLevel[enchantLevel], arrow.getLocation(), explosionParticleAmountPerLevel[enchantLevel]);
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

        this.attemptEnchantExecution(args);
    }
}

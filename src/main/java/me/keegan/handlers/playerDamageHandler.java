package me.keegan.handlers;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class playerDamageHandler implements Listener {
    private static playerDamageHandler instance;
    private final HashMap<EntityDamageEvent, Double> multiplicativeDamage = new HashMap<>();
    private final HashMap<EntityDamageEvent, Double> additiveDamage = new HashMap<>();
    private final HashMap<EntityDamageEvent, Double> reductionDamage = new HashMap<>();
    private final HashMap<EntityDamageEvent, Double> trueDamage = new HashMap<>();

    public static playerDamageHandler getInstance() {
        if (instance == null) {
            instance = new playerDamageHandler();
        }

        return instance;
    }

    public Double calculateNewDamage(EntityDamageByEntityEvent e, double finalDamage) {
        /*
         * Damage is calculated additively.
         * How damage works:
         *
         * Subtract the reduction damage from the additive damage
         * For example, -100.0% reduction + 50.0% additive = -50.0% enchant damage
         * Then, multiply the *absolute* enchant damage by the final damage
         * And subtract that number by the final damage if its negative,
         * If positive, add it to the final damage.
         *
         * Multiplicative damage is not in The Hypixel Pit.
         */

        /*
         * Final damage is 0 if there is any absorption, however absorption is still calculated after all effects and armor
         * So absorption is the final damage but negative if there is any absorption present
         */

        finalDamage += Math.abs(e.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION));

        if (this.additiveDamage.getOrDefault(e, 0.0) == 0.0 && this.reductionDamage.getOrDefault(e, 0.0) == 0.0) { return finalDamage; }

        double enchantAdditivePercent
                = (this.additiveDamage.getOrDefault(e, 0.0) < this.reductionDamage.getOrDefault(e, 0.0))
                ? (this.reductionDamage.getOrDefault(e, 0.0) - this.additiveDamage.getOrDefault(e, 0.0)) / 100
                : (this.additiveDamage.getOrDefault(e, 0.0) - this.reductionDamage.getOrDefault(e, 0.0)) / 100;
        double enchantAdditive = Math.abs(enchantAdditivePercent) * finalDamage; // -0.5% * 5 = -2.5 reduction | 0.5% * 5 = 2.5 damage

        // new damage cannot be negative, so use Math.max
        return (enchantAdditivePercent <= 0.0) ? Math.max(0.0, finalDamage - enchantAdditive)  : finalDamage + enchantAdditive;
    }

    public Double calculateTrueDamage(EntityDamageByEntityEvent e) {
        // implements mirrors later on
        return this.getTrueDamage(e);
    }

    private void resetDamageValues(EntityDamageByEntityEvent e) {
        this.multiplicativeDamage.remove(e);
        this.multiplicativeDamage.remove(e);
        this.multiplicativeDamage.remove(e);
        this.trueDamage.remove(e);
    }

    public Double getMultiplicativeDamage(EntityDamageByEntityEvent e) {
        return this.multiplicativeDamage.getOrDefault(e, 1.0);
    }

    public Double getDamage(EntityDamageByEntityEvent e) {
        return this.additiveDamage.getOrDefault(e, 0.0);
    }

    public Double getTrueDamage(EntityDamageByEntityEvent e) {
        return this.trueDamage.getOrDefault(e, 0.0);
    }

    public Double getReductionDamage(EntityDamageByEntityEvent e) {
        return this.reductionDamage.getOrDefault(e, 0.0);
    }

    public void addMultiplicativeDamage(EntityDamageByEntityEvent e, double damage) {
        this.multiplicativeDamage.put(e, this.getMultiplicativeDamage(e) + damage);
    }

    public void addDamage(EntityDamageByEntityEvent e, double damage) {
        this.additiveDamage.put(e, this.getDamage(e) + damage);
    }

    public void addTrueDamage(EntityDamageByEntityEvent e, double damage) {
        this.trueDamage.put(e, this.getTrueDamage(e) + damage);
    }

    public void reduceDamage(EntityDamageByEntityEvent e, double damage) {
        this.reductionDamage.put(e, this.getReductionDamage(e) + damage);
    }

    // executed last
    // use playerDamagerHandler.instance, otherwise you are calculating values from this class which is
    // not used by other enchantments; aka they won't exist
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDamaged(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity) || !(e.getDamager() instanceof LivingEntity)) { return; }
        LivingEntity damaged = (LivingEntity) e.getEntity();

        double calculatedDamage = playerDamageHandler.getInstance().calculateNewDamage(e, e.getFinalDamage());
        double trueDamage = playerDamageHandler.instance.calculateTrueDamage(e);

        e.setDamage(calculatedDamage);
        damaged.setHealth(Math.max(0, Math.min(damaged.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), damaged.getHealth() - trueDamage)));

        playerDamageHandler.instance.resetDamageValues(e);
    }
}

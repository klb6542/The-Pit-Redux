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
    private final HashMap<EntityDamageByEntityEvent, Double> additiveDamage = new HashMap<>();
    private final HashMap<EntityDamageByEntityEvent, Double> reductionDamage = new HashMap<>();
    private final HashMap<EntityDamageByEntityEvent, Double> maxDamage = new HashMap<>();
    private final HashMap<EntityDamageByEntityEvent, Double> trueDamage = new HashMap<>();

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
         */

        /*
         * Final damage is 0 if there is any absorption,
         * however absorption is still calculated after all effects and armor just like final damage.
         *
         * So absorption is the final damage but negative if there is any present
         * if absorption is not present, it will be 0
         */

        finalDamage += Math.abs(e.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION));

        if (this.getDamage(e) == 0.0
                && this.getReductionDamage(e) == 0.0
                && this.getMaxDamage(e) == Math.E) { return finalDamage; }

        double enchantAdditivePercent
                = (this.getReductionDamage(e) > this.getDamage(e))
                ? -((this.getReductionDamage(e) - this.getDamage(e)) / 100)
                : (this.getDamage(e) - this.getReductionDamage(e)) / 100;
        double enchantAdditive = enchantAdditivePercent * finalDamage; // -0.5% * 5 = -2.5 reduction | 0.5% * 5 = 2.5 damage
        double newFinalDamage = Math.max(0.0, enchantAdditive + finalDamage);

        // return the new final damage clamped with the max damage
        return Math.max(0.0, Math.min(this.getMaxDamage(e), newFinalDamage));
    }

    public Double calculateTrueDamage(EntityDamageByEntityEvent e) {
        // implement mirrors later on
        return this.getTrueDamage(e);
    }

    private void resetDamageValues(EntityDamageByEntityEvent e) {
        this.additiveDamage.remove(e);
        this.reductionDamage.remove(e);
        this.maxDamage.remove(e);
        this.trueDamage.remove(e);
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

    public Double getMaxDamage(EntityDamageByEntityEvent e) {
        return this.maxDamage.getOrDefault(e, Math.E);
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

    public void setMaxDamage(EntityDamageByEntityEvent e, double damage) {
        this.maxDamage.put(e, damage);
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

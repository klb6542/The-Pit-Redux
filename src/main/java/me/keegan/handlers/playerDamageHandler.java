package me.keegan.handlers;

import me.keegan.pitredux.ThePitRedux;
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

    public static playerDamageHandler getInstance() {
        if (instance == null) {
            instance = new playerDamageHandler();
        }

        return instance;
    }

    private Double calculateNewDamage(EntityDamageByEntityEvent e, Double finalDamage) {
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

        if (this.additiveDamage.getOrDefault(e, 0.0) == 0.0 && this.reductionDamage.getOrDefault(e, 0.0) == 0.0) { return finalDamage; }

        double enchantAdditivePercent
                = (this.additiveDamage.getOrDefault(e, 0.0) < this.reductionDamage.getOrDefault(e, 0.0))
                ? (this.reductionDamage.getOrDefault(e, 0.0) - this.additiveDamage.getOrDefault(e, 0.0)) / 100
                : (this.additiveDamage.getOrDefault(e, 0.0) - this.reductionDamage.getOrDefault(e, 0.0)) / 100;
        double enchantAdditive = Math.abs(enchantAdditivePercent) * finalDamage; // -0.5% * 5 = -2.5 reduction | 0.5% * 5 = 2.5 damage

        // new damage cannot be negative, so use Math.max
        return (enchantAdditivePercent <= 0.0) ? Math.max(0.0, finalDamage - enchantAdditive) : finalDamage + enchantAdditive;
    }

    private void resetDamageValues(EntityDamageByEntityEvent e) {
        this.multiplicativeDamage.put(e, 1.0);
        this.additiveDamage.put(e, 0.0);
        this.reductionDamage.put(e, 0.0);
    }

    public void addMultiplicativeDamage(EntityDamageByEntityEvent e, Double damage) {
        this.multiplicativeDamage.put(e, this.multiplicativeDamage.getOrDefault(e, 1.0));
    }

    public void addDamage(EntityDamageByEntityEvent e, Double damage) {
        this.additiveDamage.put(e, this.additiveDamage.getOrDefault(e, 0.0));
    }

    public void reduceDamage(EntityDamageByEntityEvent e, Double damage) {
        this.reductionDamage.put(e, this.reductionDamage.getOrDefault(e, 0.0));
    }

    // executed last
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDamaged(EntityDamageByEntityEvent e) {
        LivingEntity entity = (LivingEntity) e.getEntity();
        Double newFinalDamage = calculateNewDamage(e, e.getFinalDamage());

        e.setDamage(newFinalDamage);
        resetDamageValues(e);

        ThePitRedux.getPlugin().getLogger().info("Priority High");
    }
}

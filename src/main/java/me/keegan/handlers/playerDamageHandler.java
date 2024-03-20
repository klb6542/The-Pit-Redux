package me.keegan.handlers;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class playerDamageHandler implements Listener {
    private static playerDamageHandler instance;
    private Double multiplicativeDamage = 1.0;
    private Double additiveDamage = 0.0;
    private Double reductionDamage = 0.0;

    public static playerDamageHandler getInstance() {
        if (instance == null) {
            instance = new playerDamageHandler();
        }

        return instance;
    }

    private Double calculateNewDamage(Double finalDamage) {
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

        if (this.additiveDamage == 0.0 && this.reductionDamage == 0.0) { return finalDamage; }

        double enchantAdditivePercent = (this.additiveDamage < this.reductionDamage)
                ? (this.reductionDamage - this.additiveDamage) / 100
                : (this.additiveDamage - this.reductionDamage) / 100;
        double enchantAdditive = Math.abs(enchantAdditivePercent) * finalDamage; // -0.5% * 5 = -2.5 reduction | 0.5% * 5 = 2.5 damage

        // new damage cannot be negative, so use Math.max
        return (enchantAdditivePercent <= 0.0) ? Math.max(0.0, finalDamage - enchantAdditive) : finalDamage + enchantAdditive;
    }

    private void resetDamageValues() {
        this.multiplicativeDamage = 1.0;
        this.additiveDamage = 0.0;
        this.reductionDamage = 0.0;
    }

    public void addMultiplicativeDamage(Double damage) {
        this.multiplicativeDamage += damage;
    }

    public void addDamage(Double damage) {
        this.additiveDamage += damage;
    }

    public void reduceDamage(Double damage) {
        this.reductionDamage -= damage;
    }

    // executed last
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDamaged(EntityDamageByEntityEvent e) {
        LivingEntity entity = (LivingEntity) e.getEntity();
        Double newFinalDamage = calculateNewDamage(e.getFinalDamage());

        e.setDamage(newFinalDamage);
        resetDamageValues();

        ThePitRedux.getPlugin().getLogger().info("Priority High");
    }
}

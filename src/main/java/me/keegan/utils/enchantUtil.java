package me.keegan.utils;

/*
 * Copyright (c) 2024. Created by klb.
 */

import me.keegan.enums.cooldownEnums;
import me.keegan.pitredux.ThePitRedux;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class enchantUtil implements Listener {
    private final List<UUID> cooldown = new ArrayList<>();
    private final HashMap<UUID, Long> runnables = new HashMap<>();
    private final HashMap<UUID, Integer> hitCounter  = new HashMap<>();

    public abstract Material[] getEnchantMaterial();
    public abstract String getName();
    public abstract String[] getEnchantDescription();
    public abstract Integer getMaxLevel();
    public abstract boolean isRareEnchant();
    public abstract void executeEnchant(Object... args);

    // below is included incase there is an enchantment to reduce/cancel healing or any other methods
    public void heal(LivingEntity entity, Double amount) {
       entity.setHealth(Math.max(0.0, Math.min(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), amount + entity.getHealth())));
    }

    public void setSpeed(LivingEntity entity, Integer amplifier, Integer duration) {
        // times 20 because 20 ticks = 1 second
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, amplifier));
    }

    public void setJumpBoost(LivingEntity entity, Integer amplifier, Integer duration) {
        // times 20 because 20 ticks = 1 second
        entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration * 20, amplifier));
    }

    /*
     -------------------------------------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------------------------------------
     */

    private Boolean runnableExists(UUID uuid, Long uniqueValue) {
        // if a runnable exists and if the runnable can find itself then it has not been over-written
        return this.runnables.containsKey(uuid) && this.runnables.get(uuid).equals(uniqueValue);
    }

    private Boolean canOverrideRunnable(cooldownEnums cooldownEnum) {
        return cooldownEnum == cooldownEnums.OVERRIDE
                || cooldownEnum == cooldownEnums.RESET_HIT_COUNTER;
    }

    public Integer getHitCounter(UUID uuid) {
        return this.hitCounter.getOrDefault(uuid, 0);
    }

    public void resetHitCounter(UUID uuid) {
        this.hitCounter.remove(uuid);
    }

    public void addHitToHitCounter(UUID uuid, Integer hitAmount) {
        this.hitCounter.put(uuid, this.hitCounter.getOrDefault(uuid, 0) + hitAmount);
    }

    public boolean isOnCooldown(UUID uuid) {
        return this.cooldown.contains(uuid);
    }

    public void removeCooldown(UUID uuid) {
        this.cooldown.remove(uuid);
    }

    public void addCooldown(UUID uuid, Long duration, cooldownEnums cooldownEnum) {
        // return if duplicates are found
        if (cooldownEnum == cooldownEnums.NORMAL) {
            if (this.isOnCooldown(uuid)){
                return;
            }else{
                this.cooldown.add(uuid);
            }
        }

        Long runnableUniqueValue = new Random().nextLong();

        if (canOverrideRunnable(cooldownEnum)) {
            if (!this.isOnCooldown(uuid)){
                this.cooldown.add(uuid);
            }

            // remove runnable that is currently running
            this.runnables.remove(uuid);

            // generate a random long for the uniqueValue that will represent the current runnable
            this.runnables.put(uuid, runnableUniqueValue);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                // if the current runnable has been over-written, then return because another runnable is running in its place
                if (!runnableExists(uuid, runnableUniqueValue) && canOverrideRunnable(cooldownEnum)) { return; }

                removeCooldown(uuid);

                if (cooldownEnum == cooldownEnums.RESET_HIT_COUNTER) {
                    resetHitCounter(uuid);
                }
            }

        }.runTaskLater(ThePitRedux.getPlugin(), duration * 20);
    }

    /*
     * Object[] args contains:

     * args[0] = event
     * args[1] = itemstack to check for enchant
     * args[2] = enchant that is being attempted to execute
     -------------------------------------------------------------------------------------------------------------------
     * Object[] args returns:

     * args[0] = event
     * args[1] = itemstack
     * args[2] = enchant level - 1
     */

    public void attemptEnchantExecution(Object[] args) {
        if (!mysticUtil.getInstance().hasEnchant((ItemStack) args[1], (enchantUtil) args[2])) { return; }

        args[2] = mysticUtil.getInstance().getEnchantLevel((ItemStack) args[1], (enchantUtil) args[2]) - 1;
        this.executeEnchant(args);
    }
}

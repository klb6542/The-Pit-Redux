package me.keegan.utils;

/*
 * Copyright (c) 2024. Created by klb.
 */

import me.keegan.enums.cooldownEnums;
import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.keegan.lobby.spawn.spawnHandler.isInSpawn;

public abstract class enchantUtil implements Listener {
    private final List<UUID> cooldown = new ArrayList<>();
    private final HashMap<UUID, Long> runnables = new HashMap<>();
    private final HashMap<UUID, Integer> hitCounter  = new HashMap<>();

    public abstract Material[] getEnchantMaterial();
    public abstract mysticEnums getEnchantType();
    public abstract String getName();
    public abstract String[] getEnchantDescription();
    public abstract Integer getMaxLevel();
    public abstract boolean isRareEnchant();
    public abstract boolean isMysticWellEnchant(); // can items be enchanted with it in the mystic well
    public abstract void executeEnchant(Object[] args);

    // below is included incase there is an enchantment to reduce/cancel healing or any other methods
    public void heal(LivingEntity entity, Double amount) {
       entity.setHealth(Math.max(0.0, Math.min(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), amount + entity.getHealth())));
    }

    public void setSpeed(LivingEntity entity, Integer amplifier, Double duration) {
        // times 20 because 20 ticks = 1 second
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (duration * 20), amplifier));
    }

    public void slowdown(LivingEntity entity, Integer amplifier, Double duration) {
        // times 20 because 20 ticks = 1 second
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (duration * 20), amplifier));
    }

    public void setJumpBoost(LivingEntity entity, Integer amplifier, Double duration) {
        // times 20 because 20 ticks = 1 second
        entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (int) (duration * 20), amplifier));
    }


    public void addPotionEffect(LivingEntity entity, PotionEffectType potionEffectType, Integer amplifier, Double duration) {
        // times 20 because 20 ticks = 1 second
        entity.addPotionEffect(new PotionEffect(potionEffectType, (int) (duration * 20), amplifier));
    }

    // entity is the target
    public void createExplosionEffect(LivingEntity entity, Location location) {
        entity.setVelocity(entity.getLocation().toVector().subtract(location.toVector()).normalize());
    }

    public double getDistance(LivingEntity livingEntity, LivingEntity livingEntity2) {
        Location location = livingEntity.getLocation();
        Location location2 = livingEntity2.getLocation();

        return Math.sqrt((Math.pow(Math.abs(location2.getX() - location.getX()), 2)) + (Math.pow(Math.abs(location2.getZ() - location.getZ()), 2)));
    }

    public boolean isInAir(LivingEntity livingEntity) {
        return !livingEntity.isOnGround() && livingEntity.getFallDistance() > 0.0f;
    }

    public boolean isCriticalHit(LivingEntity livingEntity) {
        return isInAir(livingEntity)
                && livingEntity.getVehicle() == null
                && !livingEntity.hasPotionEffect(PotionEffectType.BLINDNESS)
                && livingEntity.getLocation().getBlock().getType() != Material.LADDER
                && livingEntity.getLocation().getBlock().getType() != Material.VINE
                && livingEntity.getLocation().getBlock().getType() != Material.WATER
                && livingEntity.getLocation().getBlock().getType() != Material.LAVA
                && livingEntity.getLocation().getBlock().getType() != Material.WEEPING_VINES
                && livingEntity.getLocation().getBlock().getType() != Material.TWISTING_VINES;
    }

    public boolean isNearWater(Location location, int blockRange) {
        World world = location.getWorld();
        int blockX = location.getBlockX();
        int blockY = location.getBlockY();
        int blockZ = location.getBlockZ();

        for (int x = blockX - blockRange; x <= blockX + blockRange; x++) {
            for (int y = blockY - blockRange; y <= blockY + blockRange; y++) {
                for (int z = blockZ - blockRange; z <= blockZ + blockRange; z++) {
                    if (new Location(world, x, y, z).getBlock().getType() != Material.WATER) { continue; }

                    return true;
                }
            }
        }

        return false;
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

    public boolean attemptEnchantExecution(LivingEntity target, Object[] args) {
        if (args[1] == null
                || !mysticUtil.getInstance().hasEnchant((ItemStack) args[1], (enchantUtil) args[2])
                || isInSpawn(target)) { return false; }

        args[2] = mysticUtil.getInstance().getEnchantLevel((ItemStack) args[1], (enchantUtil) args[2]) - 1;
        this.executeEnchant(args);

        return true;
    }

    public int getEnchantLevel(ItemStack itemStack, enchantUtil enchant) {
        if (itemStack == null || enchant == null) { return -1; }

        return mysticUtil.getInstance().getEnchantLevel(itemStack, enchant) - 1;
    }
}

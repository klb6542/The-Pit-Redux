package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.mysticUtil;
import me.keegan.utils.propertiesUtil;
import me.keegan.utils.setupUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;

public class SnowmenArmy extends enchantUtil implements setupUtils {
    private static final List<Snowman> currentSnowman = new ArrayList<>();

    private final Integer[] snowmanDurationTime = new Integer[]{45, 75, 125};
    private final Integer scoopConsumableAmount = 3;

    private final Integer snowmanHealth = 10;
    private final Integer snowmanDamage = 3;

    // used only for checking if itemstack is similar to snowman scoop
    private Boolean isSimilar(ItemStack itemStack, ItemStack itemStack2) {
        ItemStack copyItemStack = itemStack.clone();
        ItemStack copyItemStack2 = itemStack2.clone();

        ItemMeta itemMeta = copyItemStack.getItemMeta();
        ItemMeta itemMeta2 = copyItemStack2.getItemMeta();

        if (itemMeta != null && itemMeta.getLore() != null) {
            List<String> lore = itemMeta.getLore();
            lore.remove(lore.size() - 1);

            itemMeta.setLore(lore);
        }

        if (itemMeta2 != null && itemMeta2.getLore() != null) {
            List<String> lore = itemMeta2.getLore();
            lore.remove(lore.size() - 1);

            itemMeta2.setLore(lore);
        }

        copyItemStack.setItemMeta(itemMeta);
        copyItemStack2.setItemMeta(itemMeta2);

        return copyItemStack.isSimilar(copyItemStack2);
    }

    private Integer getSnowmanDuration(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();

        return Integer.valueOf(lore.get(lore.size() - 1).split(" ")[3]);
    }

    private Snowman createSnowman(World world, Location spawnLocation) {
        Snowman snowman = (Snowman) world.spawnEntity(spawnLocation, EntityType.SNOWMAN);

        snowman.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(snowmanHealth);
        snowman.setHealth(snowmanHealth);

        currentSnowman.add(snowman);
        return snowman;
    }

    private ItemStack createScoop(int durationTime) {
        ItemStack itemStack = new ItemStack(Material.SNOW_BLOCK);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(aqua + "Snowman Scoop");

        List<String> lore = new ArrayList<>();
        lore.add(gray + "Enchant item");
        lore.add(gray + "Place this block to spawn");
        lore.add(gray + "a snowman. Consumes " + scoopConsumableAmount + " blocks");
        lore.add(gray + "per snowman.");
        lore.add(gray + "Snowman lasts for " + durationTime + " seconds");

        itemMeta.setLore(lore);
        propertiesUtil.setProperty(propertiesUtil.notPlaceable, itemMeta);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.LEATHER_LEGGINGS};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.NORMAL;
    }

    @Override
    public String getName() {
        return "Snowmen Army";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Gain{1} +1 scoop{0} on kill. Placing the/n" +
                                "{0}scoop will consume {3} of them to spawn/n" +
                                "{0}a{1} snowman{0}, defending you for {2}{4}/n" +
                                "{0}seconds.",
                        gray, aqua, green, scoopConsumableAmount, snowmanDurationTime[0]),

                MessageFormat.format("{0}Gain{1} +1 scoop{0} on kill. Placing the/n" +
                                "{0}scoop will consume {3} of them to spawn/n" +
                                "{0}a{1} snowman{0}, defending you for {2}{4}/n" +
                                "{0}seconds.",
                        gray, aqua, green, scoopConsumableAmount, snowmanDurationTime[1]),

                MessageFormat.format("{0}Gain{1} +1 scoop{0} on kill. Placing the/n" +
                                "{0}scoop will consume {3} of them to spawn/n" +
                                "{0}a{1} snowman{0}, defending you for {2}{4}/n" +
                                "{0}seconds.",
                        gray, aqua, green, scoopConsumableAmount, snowmanDurationTime[2])
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
        return false;
    }

    @Override
    public void executeEnchant(Object[] args) {
        if (args[2] != null) { return; }
        BlockPlaceEvent e = (BlockPlaceEvent) args[0];

        Snowman snowman = this.createSnowman(e.getPlayer().getWorld(), e.getBlock().getLocation());

        new BukkitRunnable() {

            @Override
            public void run() {
                if (!snowman.isDead()) {
                    snowman.remove();
                }

                currentSnowman.remove(snowman);
            }

        }.runTaskLater(ThePitRedux.getPlugin(), this.getSnowmanDuration((ItemStack) args[1]) * 20);
    }

    @EventHandler
    public void entityDied(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) { return; }
        Player killer = e.getEntity().getKiller();

        Object[] args = new Object[]{
                e,
                (killer.getEquipment() != null) ? killer.getEquipment().getLeggings() : null,
                this,
        };

        boolean success = this.attemptEnchantExecution(args);
        if (!success) { return; }
        if (killer.getInventory().firstEmpty() == -1) { return; }

        killer.getInventory().addItem(this.createScoop(
                snowmanDurationTime[mysticUtil.getInstance().getEnchantLevel(killer.getEquipment().getLeggings(), this) - 1]
        ));
    }

    @EventHandler
    public void entityDied2(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Snowman)
                || e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()
                != snowmanHealth) { return; }

        e.setDroppedExp(0);
        e.getDrops().clear();
    }

    @EventHandler
    public void blockPlaced(BlockPlaceEvent e) {
        if (!this.isSimilar(e.getItemInHand(), this.createScoop(0))) { return; }
        if (e.getItemInHand().getAmount() < scoopConsumableAmount) { return; }

        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.getItemInHand().setAmount(e.getItemInHand().getAmount() - scoopConsumableAmount);
        }

        Object[] args = new Object[]{
                e,
                e.getItemInHand(),
                null,
        };

        this.executeEnchant(args);
    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Snowball)
                || !(((Snowball) e.getDamager()).getShooter() instanceof Snowman)
                || !((e.getEntity()) instanceof LivingEntity)) { return; }

        // the playerDamageHandler will pick this up and 3.0 will be the final damage
        // so it can be calculated with mystics
        e.setDamage(snowmanDamage);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        currentSnowman.forEach(snowman -> {
            currentSnowman.remove(snowman);
            snowman.remove();
        });
    }
}

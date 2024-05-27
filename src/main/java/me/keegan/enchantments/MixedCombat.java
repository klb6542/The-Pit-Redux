package me.keegan.enchantments;

import me.keegan.classes.MultiMap;
import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import me.keegan.utils.entityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;

public class MixedCombat extends enchantUtil {
    // KEY, VALUE, BONUS
    private final static MultiMap<UUID, UUID, Integer> extraDamages = new MultiMap<>(UUID.class, Integer.class);
    private final Integer[] damagePerLevel = new Integer[]{15, 25, 35};

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
        return "Mixed Combat";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Shooting an enemy empowers your/n"
                        + "{0}next melee strike against them for/n"
                        + "{1}+{2}%{0} extra damage",
                        gray, red, damagePerLevel[0]),

                MessageFormat.format("{0}Shooting an enemy empowers your/n"
                                + "{0}next melee strike against them for/n"
                                + "{1}+{2}%{0} extra damage",
                        gray, red, damagePerLevel[1]),

                MessageFormat.format("{0}Shooting an enemy empowers your/n"
                                + "{0}next melee strike against them for/n"
                                + "{1}+{2}%{0} extra damage",
                        gray, red, damagePerLevel[2])
        };
    }

    @Override
    public Integer getMaxLevel() {
        return this.getEnchantDescription().length;
    }

    @Override
    public boolean isRareEnchant() {
        return false;
    }

    @Override
    public boolean isMysticWellEnchant() {
        return true;
    }

    @Override
    public void executeEnchant(Object[] args) {
        if (args.length == 4) { return; }

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        LivingEntity damager = (LivingEntity) e.getDamager();
        LivingEntity damaged = (LivingEntity) e.getEntity();

        if (!extraDamages.containsKey(damager.getUniqueId())
                || extraDamages.getValue(damager.getUniqueId()) != damaged.getUniqueId()) { return; }

        Integer enchantLevel = extraDamages.getBonus(damager.getUniqueId());
        if (enchantLevel == null) { return; }

        ThePitRedux.getPlugin().getLogger().info("Doing extra damage!");

        extraDamages.resetMaps(damager.getUniqueId());
        playerDamageHandler.getInstance().addDamage(e, damagePerLevel[enchantLevel]);
    }

    @EventHandler
    public void arrowDamaged(EntityDamageByEntityEvent e) {
        if (!entityUtil.damagerIsArrow(e)) { return; }

        Arrow arrow = (Arrow) e.getDamager();
        LivingEntity shooter = (LivingEntity) arrow.getShooter();

        @Nullable ItemStack bow = (shooter.getEquipment() != null && shooter.getEquipment().getItemInMainHand().getType() == Material.BOW)
                ? shooter.getEquipment().getItemInMainHand()
                : shooter.getEquipment().getItemInOffHand();

        Object[] args = new Object[]{
                e,
                bow,
                this,
                true
        };

        boolean success = this.attemptEnchantExecution(shooter, args);
        if (!success) { return; }

        extraDamages.put(shooter.getUniqueId(),
                e.getEntity().getUniqueId(),
                this.getEnchantLevel(bow, this));
    }

    // runs after damage is put in hashmap
    @EventHandler(priority = EventPriority.HIGH)
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity) || !(e.getDamager() instanceof LivingEntity)) { return; }

        LivingEntity damager = (LivingEntity) e.getDamager();

        Object[] args = new Object[]{
                e,
                (damager.getEquipment() != null) ? damager.getEquipment().getItemInMainHand() : null,
                this
        };

        this.attemptEnchantExecution(damager, args);
    }
}

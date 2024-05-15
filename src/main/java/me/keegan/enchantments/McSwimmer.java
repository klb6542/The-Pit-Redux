package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static me.keegan.utils.formatUtil.*;

public class McSwimmer extends enchantUtil {
    private final List<Material> liquids = new ArrayList<Material>(){{
        add(Material.WATER);
        add(Material.LAVA);
    }};

    private final Integer[] damageReductionPerLevel = new Integer[]{25, 40, 65};

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
        return "McSwimmer";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Receive {1}-{2}%{0} melee damage/n"
                        + "{0}while swimming in water or lava",
                        gray, blue, damageReductionPerLevel[0]),

                MessageFormat.format("{0}Receive {1}-{2}%{0} melee damage/n"
                                + "{0}while swimming in water or lava",
                        gray, blue, damageReductionPerLevel[1]),

                MessageFormat.format("{0}Receive {1}-{2}%{0} melee damage/n"
                                + "{0}while swimming in water or lava",
                        gray, blue, damageReductionPerLevel[2])
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
        int enchantLevel = (int) args[2];

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        LivingEntity damaged = (LivingEntity) e.getEntity();
        if (!liquids.contains(damaged.getLocation().getBlock().getType())) { return; }

        playerDamageHandler.getInstance().reduceDamage(e, damageReductionPerLevel[enchantLevel]);
    }

    @EventHandler
    public void entityDamaged(EntityDamageByEntityEvent e) {
        if ((!(e.getEntity() instanceof LivingEntity) || !(e.getDamager() instanceof LivingEntity))) { return; }

        LivingEntity damaged = (LivingEntity) e.getEntity();

        Object[] args = new Object[]{
                e,
                (damaged.getEquipment() != null) ? damaged.getEquipment().getLeggings() : null,
                this
        };

        this.attemptEnchantExecution(args);
    }
}

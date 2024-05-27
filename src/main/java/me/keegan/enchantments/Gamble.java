package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.handlers.playerDamageHandler;
import me.keegan.utils.enchantUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.MessageFormat;
import java.util.Random;

import static me.keegan.utils.formatUtil.*;

public class Gamble extends enchantUtil {
    private final Integer[] trueDamageHeartsPerLevel = new Integer[]{1, 2, 3};

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{Material.GOLDEN_SWORD};
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.NORMAL;
    }

    @Override
    public String getName() {
        return "Gamble";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}50-50 chance{1} to deal {2}{4}❤{1} true damage/n"
                        + "{1}to whoever you hit, or to yourself./n"
                        + "{3}Self-damage is unconditional", lightPurple, gray, red,
                        darkGray, trueDamageHeartsPerLevel[0]),

                MessageFormat.format("{0}50-50 chance{1} to deal {2}{4}❤{1} true damage/n"
                                + "{1}to whoever you hit, or to yourself./n"
                                + "{3}Self-damage is unconditional", lightPurple, gray, red,
                        darkGray, trueDamageHeartsPerLevel[1]),

                MessageFormat.format("{0}50-50 chance{1} to deal {2}{4}❤{1} true damage/n"
                                + "{1}to whoever you hit, or to yourself./n"
                                + "{3}Self-damage is unconditional", lightPurple, gray, red,
                        darkGray, trueDamageHeartsPerLevel[2]),
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

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        LivingEntity damaged = (LivingEntity) e.getEntity();
        LivingEntity damager = (LivingEntity) e.getDamager();

        if (new Random().nextInt(2) == 0) {
            playerDamageHandler.getInstance().doTrueDamageIgnoreMirror(damager, damager, trueDamageHeartsPerLevel[enchantLevel]);

            if (!(damaged instanceof Player)) { return; }
            ((Player) damaged).playSound(damager.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
        }else{
            playerDamageHandler.getInstance().doTrueDamageIgnoreMirror(damaged, damager, trueDamageHeartsPerLevel[enchantLevel]);

            if (!(damaged instanceof Player)) { return; }
            ((Player) damaged).playSound(damager.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
        }
    }

    @EventHandler
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

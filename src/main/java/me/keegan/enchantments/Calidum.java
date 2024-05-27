package me.keegan.enchantments;

import me.keegan.enums.mysticEnums;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.enchantUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static me.keegan.utils.formatUtil.*;

public class Calidum extends enchantUtil {
    private final Double chanceToProc = 0.75;

    @Override
    public Material[] getEnchantMaterial() {
        return new Material[]{
                Material.NETHERITE_PICKAXE
        };
    }

    @Override
    public mysticEnums getEnchantType() {
        return mysticEnums.PICKAXE;
    }

    @Override
    public String getName() {
        return "Calidum";
    }

    @Override
    public String[] getEnchantDescription() {
        return new String[]{
                MessageFormat.format("{0}Mining blocks has a {1}{2}% chance/n"
                        + "{0}to mine nearby blocks",
                        gray, darkAqua, chanceToProc * 100)
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
        return false;
    }

    private void createParticles(Player player, Block block) {
        Location location = block.getLocation().add(1, 1, 1);

        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.TEAL, 1.0f);
        double dustPeriod = 0.2;

        for (double x = 0; x <= 1.0; x += dustPeriod) {
            for (double y = 0; y <= 1.0; y += dustPeriod) {
                for (double z = 0; z <= 1.0; z += dustPeriod) {
                    player.spawnParticle(Particle.REDSTONE, location.clone().subtract(x, y, z), 1, dustOptions);
                }
            }
        }
    }

    @Override
    public void executeEnchant(Object[] args) {
        if (Math.random() >= chanceToProc) { return; }

        BlockBreakEvent e = (BlockBreakEvent) args[0];
        Player player = e.getPlayer();
        ItemStack itemStack = player.getEquipment().getItemInMainHand();

        Location location = e.getBlock().getLocation();
        World world = location.getWorld();
        int locationX = (int) location.getX();
        int locationY = (int) location.getY();
        int locationZ = (int) location.getZ();

        List<Block> blocksNearby = new ArrayList<>(Arrays.asList(
                world.getBlockAt(locationX + 1, locationY, locationZ),
                world.getBlockAt(locationX - 1, locationY, locationZ),
                world.getBlockAt(locationX, locationY + 1, locationZ),
                world.getBlockAt(locationX, locationY - 1, locationZ),
                world.getBlockAt(locationX, locationY, locationZ + 1),
                world.getBlockAt(locationX, locationY, locationZ - 1)
        ));

        blocksNearby = blocksNearby
                .stream()
                .filter(block -> block.getType() != Material.AIR && block.getType() == e.getBlock().getType())
                .collect(Collectors.toList());

        if (blocksNearby.isEmpty()) { return; }
        Block chosenBlock = blocksNearby.get(new Random().nextInt(blocksNearby.size()));

        this.createParticles(player, chosenBlock);
        world.playSound(player.getLocation(), chosenBlock.getBlockData().getSoundGroup().getBreakSound(), 1f, 1f);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(chosenBlock, player);
        ThePitRedux.getPlugin().getServer().getPluginManager().callEvent(blockBreakEvent);

        chosenBlock.breakNaturally(itemStack);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void blockMined(BlockBreakEvent e) {
        if (e.isCancelled()) { return; }

        Player player = e.getPlayer();

        Object[] args = new Object[]{
                e,
                (player.getEquipment() != null) ? player.getEquipment().getItemInMainHand() : null,
                this
        };

        this.attemptEnchantExecution(player, args);
    }
}
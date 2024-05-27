package me.keegan.items.lame;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.itemUtil;
import me.keegan.utils.protocolUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static me.keegan.utils.formatUtil.*;
import static me.keegan.utils.itemStackUtil.isLivingEntityHoldingItemStack;
import static me.keegan.utils.itemStackUtil.isSimilar;

public class first_aid_egg extends itemUtil implements protocolUtil {
    private static final List<UUID> cooldowns = new ArrayList<>();
    private final Double healAmountInHearts = 2.5;
    private final Integer cooldownDuration = 15;

    @Override
    public String getNamespaceName() {
        return "first_aid_egg";
    }

    @Override
    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(Material.MOOSHROOM_SPAWN_EGG);
        SpawnEggMeta itemMeta = (SpawnEggMeta) itemStack.getItemMeta();

        itemMeta.setDisplayName(red + "First-Aid Egg");
        List<String> lore = new ArrayList<>();

        lore.add(gray + "Heals " + red + healAmountInHearts + "❤");
        lore.add(gray + cooldownDuration.toString() + " second cooldown");

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private ItemStack createCooldownItem() {
        ItemStack itemStack = new ItemStack(Material.SKELETON_SPAWN_EGG);
        ItemMeta itemMeta = itemStack.getItemMeta();

        ItemStack firstAidEgg = this.createItem();
        ItemMeta firstAidEggMeta = firstAidEgg.getItemMeta();

        itemMeta.setDisplayName(gray + ChatColor.stripColor(firstAidEggMeta.getDisplayName()));

        List<String> lore = firstAidEggMeta.getLore();
        lore.add(gray + "On cooldown!");

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void createRecipe() {

    }

    @Override
    public void addPacketListener() {
        final first_aid_egg thisFirstAidEgg = this;

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ThePitRedux.getPlugin(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            // https://wiki.vg/Protocol#Entity_Sound_Effect
            // https://www.spigotmc.org/threads/tutorial-getting-packet-data-from-private-fields.164437/

            @Override
            public void onPacketSending(PacketEvent event) {
                Sound sound = event.getPacket().getSoundEffects().read(0);

                // don't put if statement in method params
                if (sound != Sound.ENTITY_SKELETON_AMBIENT
                        || !isLivingEntityHoldingItemStack(event.getPlayer(), thisFirstAidEgg.createCooldownItem())) { return; }
                event.setCancelled(true);
            }

        });
    }

    @EventHandler
    public void entityPickedUpItem(EntityPickupItemEvent e) {
        if (!isSimilar(e.getItem().getItemStack(), this.createCooldownItem())) { return; }

        e.getItem().setItemStack(this.createItem());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerInteract(PlayerInteractEvent e) {
        ItemStack itemStack = e.getItem();

        if (itemStack == null ||
                (!isSimilar(itemStack, this.createItem())
                && !isSimilar(itemStack, this.createCooldownItem()))) { return; }
        e.setCancelled(true);

        Player player = e.getPlayer();
        if (cooldowns.contains(player.getUniqueId())) { return; }
        cooldowns.add(player.getUniqueId());

        ItemStack cooldownFirstAidEgg = this.createCooldownItem();

        itemStack.setItemMeta(cooldownFirstAidEgg.getItemMeta());
        itemStack.setType(cooldownFirstAidEgg.getType());

        player.updateInventory();
        player.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 2f, 4f);
        player.setHealth(Math.min(player.getHealth() + healAmountInHearts / 2, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));

        final first_aid_egg thisFirstAidEgg = this;

        new BukkitRunnable() {

            @Override
            public void run() {
                if (e.getItem() != null) {
                    ItemStack firstAidEgg = thisFirstAidEgg.createItem();
                    ItemStack cooldownFirstAidEgg = thisFirstAidEgg.createCooldownItem();

                    Arrays.stream(player.getInventory().getContents())
                            .forEach(item -> {{
                                if (item != null && isSimilar(item, cooldownFirstAidEgg)) {
                                    item.setItemMeta(firstAidEgg.getItemMeta());
                                    item.setType(firstAidEgg.getType());
                                }
                            }});

                    player.updateInventory();
                }

                cooldowns.remove(e.getPlayer().getUniqueId());
            }

        }.runTaskLater(ThePitRedux.getPlugin(), cooldownDuration * 20);
    }
}
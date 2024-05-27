package me.keegan.lobby;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.protocolUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.keegan.lobby.spawn.spawnHandler.isInSpawn;
import static me.keegan.utils.locationUtil.coordsIsInCoordinates;

public class lobbyHandler implements Listener, protocolUtil {
    private final List<Integer[]> lobbyCoordinates = new ArrayList<Integer[]>(){{
        add(new Integer[]{155, 143});
        add(new Integer[]{-131, 467});
    }};

    private final List<Integer[]> netherLobbyCoordinates = new ArrayList<>();

    private final List<EntityType> acceptableMobs = new ArrayList<>(Arrays.asList(
            EntityType.VILLAGER,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.WOLF,
            EntityType.SNOWMAN));

    private List<String> disabledMaterials = new ArrayList<>(Arrays.asList(
            "bucket",
            "spawn_egg"
    ));

    private boolean isInLobby(LivingEntity livingEntity) {
        Integer[] entityCoords = new Integer[]{
                (int) livingEntity.getLocation().getX(),
                (int) livingEntity.getLocation().getZ()};

        return livingEntity.getWorld().getEnvironment() == World.Environment.NORMAL
                && coordsIsInCoordinates(entityCoords, lobbyCoordinates.get(0), lobbyCoordinates.get(1));
    }

    private boolean isInNetherLobbyCoords(LivingEntity livingEntity) {
        if (netherLobbyCoordinates.isEmpty()) {
            for (Integer[] integerArray : lobbyCoordinates) {
                Integer[] middleManArray = new Integer[integerArray.length];

                for (int i = 0; i < integerArray.length; i++) {
                    middleManArray[i] = integerArray[i] / 8;
                }

                netherLobbyCoordinates.add(middleManArray);
            }
        }

        Integer[] entityCoords = new Integer[]{
                (int) livingEntity.getLocation().getX(),
                (int) livingEntity.getLocation().getZ()};

        return livingEntity.getWorld().getEnvironment() == World.Environment.NETHER
                && coordsIsInCoordinates(entityCoords, netherLobbyCoordinates.get(0), netherLobbyCoordinates.get(1));
    }

    private boolean hasBlockPermissions(LivingEntity livingEntity) {
       return (livingEntity.isOp() && isInLobby(livingEntity)) || !isInLobby(livingEntity);
    }

    private boolean containsDisabledMaterials(Material material) {
        String materialKey = material.getKey().getKey().toLowerCase();
        AtomicBoolean materialIsDisabled = new AtomicBoolean();

        disabledMaterials.forEach(string -> materialIsDisabled.set(materialKey.contains(string) || materialIsDisabled.get()));
        return materialIsDisabled.get();
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void blockBroke(BlockBreakEvent e) {
        e.setCancelled(!this.hasBlockPermissions(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void blockPlaced(@NotNull BlockPlaceEvent e) {
        if (isInNetherLobbyCoords(e.getPlayer()) && e.getBlock().getType() == Material.OBSIDIAN) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(!this.hasBlockPermissions(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerInteract(PlayerInteractEvent e) {
        e.setCancelled(!this.hasBlockPermissions(e.getPlayer())
                && containsDisabledMaterials(e.getMaterial()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerInteract(PlayerInteractEntityEvent e) {
        EntityEquipment equipmentSlot = e.getPlayer().getEquipment();

        e.setCancelled(!this.hasBlockPermissions(e.getPlayer())
                && (containsDisabledMaterials(equipmentSlot.getItemInMainHand().getType()))
                || containsDisabledMaterials(equipmentSlot.getItemInOffHand().getType()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void entityDamaged(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)) { return; }

        e.setCancelled((e.getCause() == EntityDamageEvent.DamageCause.FALL
                 && isInLobby((LivingEntity) e.getEntity()))
                || (e.getCause() != EntityDamageEvent.DamageCause.FALL
                 && isInSpawn((LivingEntity) e.getEntity())));
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void entitySpawnEvent(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)
                || e.getEntity().getWorld().getEnvironment() != World.Environment.NORMAL
                || acceptableMobs.contains(e.getEntity().getType())) { return; }
        LivingEntity livingEntity = (LivingEntity) e.getEntity();

        Integer[] entityCoords = new Integer[]{
                (int) livingEntity.getLocation().getX(),
                (int) livingEntity.getLocation().getZ()};

        e.setCancelled(coordsIsInCoordinates(entityCoords, lobbyCoordinates.get(0), lobbyCoordinates.get(1)));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void creatureSpawned(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) { return; }

        // do not put boolean in method
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void bowShot(EntityShootBowEvent e) {
        e.setCancelled(isInSpawn(e.getEntity()));
    }

    @Override
    public void addPacketListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ThePitRedux.getPlugin(), PacketType.Play.Client.USE_ENTITY) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacket().getEntityUseActions().read(0) != EnumWrappers.EntityUseAction.ATTACK
                        || !isInSpawn(event.getPlayer())) { return; }

                event.setCancelled(true);
            }

        });
    }
}

package me.keegan.utils;

import me.keegan.global.citizens;
import me.keegan.pitredux.ThePitRedux;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class npcUtil implements Listener {
    public abstract String getNPCName(); // name of actual npc in game
    public abstract void npcTriggered(Player player, NPC npc);

    public static void registerNPC(npcUtil npcUtil) {
        citizens.npcsRegistered.put(npcUtil.getNPCName(), (npcUtil::npcTriggered));

        ThePitRedux.getPlugin().getServer().getPluginManager().registerEvents(npcUtil, ThePitRedux.getPlugin());
    }
}

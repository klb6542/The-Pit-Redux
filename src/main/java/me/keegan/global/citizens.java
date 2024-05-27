package me.keegan.global;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.function.BiConsumer;

/*
 * Copyright (c) 2024. Created by klb.
 */

public class citizens implements Listener {
    // key = npc name
    public static final HashMap<String, BiConsumer<Player, NPC>> npcsRegistered = new HashMap<>();

    @EventHandler
    public void npcClicked(NPCRightClickEvent e) {
        NPC npc = e.getNPC();
        npc.getEntity().setInvulnerable(true);

        if (!npcsRegistered.containsKey(npc.getName())) { return; }
        npcsRegistered.get(npc.getName()).accept(e.getClicker(), npc);
    }
}

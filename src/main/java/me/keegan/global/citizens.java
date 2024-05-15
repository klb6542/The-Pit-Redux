package me.keegan.global;

import me.keegan.pitredux.ThePitRedux;
import me.keegan.utils.setupUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;

public class citizens implements setupUtils {

    @Override
    public void enable() {
        if (!ThePitRedux.getPlugin().Citizens) { return; }

        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        npcRegistry.forEach(npc -> ThePitRedux.getPlugin().getLogger().info(npc.getName()));
    }

    @Override
    public void disable() {

    }
}

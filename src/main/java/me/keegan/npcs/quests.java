package me.keegan.npcs;

import me.keegan.utils.npcUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

import static me.keegan.utils.formatUtil.red;

public class quests extends npcUtil {
    @Override
    public String getNPCName() {
        return "Quests";
    }

    @Override
    public void npcTriggered(Player player, NPC npc) {
        player.sendMessage(red + "Coming soon!");
    }
}

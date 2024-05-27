package me.keegan.global;

import com.mongodb.client.MongoCollection;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bson.Document;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class placeholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "pitrevamp";
    }

    @Override
    public @NotNull String getAuthor() {
        return "squeaky2137";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    /*
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if (identifier.startsWith("prestige-lb-")) {
            int place = Integer.parseInt(identifier.substring(12));

            return getLeaderboardPlace(place);
        }
        if (identifier.equals("prestige-me")) {
            Player player = player.getPlayer();
            if (player == null) { return null; }

            MongoCollection<Document> collection = mongodb.getInstance().getSurvivalCollection();
            if (collection == null) { return null; }

            Document playerDocument = mongodb.getInstance().getDocumentFromCollection(collection,
                    mongodb.getInstance().createDefaultPlayerDocument(player));
            if (playerDocument == null) { return null; }

            return playerDocument.getInteger("prestige");
        }
        return null;
    }
     */
}

package me.keegan.global;

import com.mongodb.client.*;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class mongodb  {
    private final String username = "klb";
    private final String password = "lPzawPeedWZb7pPP";
    private final Integer topPrestigeLimit = 10;

    private static mongodb instance = null;
    private MongoClient mongoClient = null;
    private MongoDatabase mongoDatabase = null;

    public static mongodb getInstance() {
        if (instance == null) {
            instance = new mongodb();
        }

        return instance;
    }

    public void startDatabaseConnection() {
        this.mongoClient = MongoClients.create(getConnectionString());
        this.mongoDatabase = this.mongoClient.getDatabase("players");
    }

    public void closeDatabaseConnection() {
        this.mongoClient.close();
    }

    public MongoClient getClient() {
        return this.mongoClient;
    }

    @Nullable
    public MongoCollection<Document> getSurvivalCollection() {
        return (this.mongoDatabase != null) ? this.mongoDatabase.getCollection("survival") : null;
    }

    @Nullable
    public MongoCollection<Document> getRemakeCollection() {
        return (this.mongoDatabase != null) ? this.mongoDatabase.getCollection("remake") : null;
    }

    @Nullable
    public Document getDocumentFromCollection(MongoCollection<Document> collection, Document document) {
        // https://www.mongodb.com/docs/drivers/java/sync/v4.3/fundamentals/builders/filters/

        return collection.find(
                eq("uuid", document.getString("uuid"))).first();
    }

    public List<Document> getDescendingPrestigeDocuments(MongoCollection<Document> collection) {
        List<Document> descendingPrestiges = new ArrayList<>();

        MongoCursor<Document> cursor = collection.find()
                .sort(descending("prestige"))
                .iterator();

        while (cursor.hasNext()) {
            descendingPrestiges.add(cursor.next());
        }

        return descendingPrestiges;
    }

    public void replaceDocumentFromCollection(MongoCollection<Document> collection, Document document) {
        if (getDocumentFromCollection(collection, document) == null) { return; }

        collection.replaceOne(eq("uuid", document.getString("uuid")), document);
    }

    public Document createDefaultPlayerDocument(Player player) {
        return new Document("uuid", player.getUniqueId().toString())
                .append("prestige", 0)
                .append("renown", 0)
                .append("perks", new ArrayList<>())
                .append("upgrades", new ArrayList<>())
                .append("renown_shop", new ArrayList<>());
    }

    private String getUsername() {
        return this.username;
    }

    private String getPassword() {
        return this.password;
    }

    private String getConnectionString() {
        return MessageFormat.format(
                "mongodb+srv://{0}:{1}@reduxcluster.yxthqzw.mongodb.net/?retryWrites=true&w=majority&appName=ReduxCluster",
                this.getUsername(), this.getPassword());
    }
}
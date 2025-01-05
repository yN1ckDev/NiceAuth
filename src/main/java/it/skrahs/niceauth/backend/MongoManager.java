package it.skrahs.niceauth.backend;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

public class MongoManager {

    private final MongoClient mongoClient;

    @Getter
    private final MongoDatabase database;


    public MongoManager(Plugin plugin) {
        String mongoUri = plugin.getConfig().getString("mongodb.uri");
        String databaseName = plugin.getConfig().getString("mongodb.database");

        if (mongoUri == null || databaseName == null) {
            throw new IllegalArgumentException("Invalid MongoDB configuration.");
        }


        this.mongoClient = MongoClients.create(mongoUri);
        this.database = mongoClient.getDatabase(databaseName);

        plugin.getLogger().info("Connessione a MongoDB stabilita con il database: " + databaseName);
    }


    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
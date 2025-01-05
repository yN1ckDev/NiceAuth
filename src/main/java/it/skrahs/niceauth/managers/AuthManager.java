package it.skrahs.niceauth.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthManager {
    private final MongoCollection<Document> collection;

    private final Set<UUID> registrationInProgressSet = new HashSet<>();
    private final Set<UUID> loginInProgressSet = new HashSet<>();

    public AuthManager(MongoDatabase database) {
        this.collection = database.getCollection("passwords");
    }

    public void savePassword(Player player, String password) {
        Document query = new Document("uuid", player.getUniqueId().toString());
        Document update = new Document("$set", new Document("password", password));
        collection.updateOne(query, update, new com.mongodb.client.model.UpdateOptions().upsert(true));
    }

    public String getPassword(Player player) {
        Document query = new Document("uuid", player.getUniqueId().toString());
        Document result = collection.find(query).first();
        return result != null ? result.getString("password") : null;
    }

    public void setRegistrationInProgress(Player player) {
        if (registrationInProgressSet.contains(player.getUniqueId())) {
            registrationInProgressSet.remove(player.getUniqueId());
        } else {
            registrationInProgressSet.add(player.getUniqueId());
        }
    }

    public void setLoginInProgress(Player player) {
        registrationInProgressSet.remove(player.getUniqueId());
        if (loginInProgressSet.contains(player.getUniqueId())) {
            loginInProgressSet.remove(player.getUniqueId());
        } else {
            loginInProgressSet.add(player.getUniqueId());
        }
    }

    public void setLoggedIn(Player player) {
        loginInProgressSet.remove(player.getUniqueId());
    }

    public boolean isRegistrationInProgress(Player player) {
        return registrationInProgressSet.contains(player.getUniqueId());
    }

    public boolean isLoginInProgress(Player player) {
        return loginInProgressSet.contains(player.getUniqueId());
    }

    public boolean isLoggedIn(Player player) {
        return !loginInProgressSet.contains(player.getUniqueId());
    }
}
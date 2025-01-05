package it.skrahs.niceauth.listener;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import it.skrahs.niceauth.NiceAuth;
import it.skrahs.niceauth.cache.ConfigCache;
import it.skrahs.niceauth.utils.ChatUtils;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JoinListener implements Listener {

    private final NiceAuth plugin;
    private final MongoCollection<Document> collection;

    public JoinListener(NiceAuth plugin) {
        this.plugin = plugin;
        this.collection = plugin.getMongoManager().getDatabase().getCollection("passwords");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Document playerDocument = collection.find(Filters.eq("uuid", player.getUniqueId().toString())).first();

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 254, false, false));
        player.setWalkSpeed(0.0f);

        if (playerDocument == null) {
            plugin.getAuthManager().setRegistrationInProgress(player);
            player.sendMessage(ChatUtils.color(ConfigCache.WELCOME_REGISTER_MESSAGE));
        } else {
            plugin.getAuthManager().setLoginInProgress(player);
            player.sendMessage(ChatUtils.color(ConfigCache.WELCOME_LOGIN_MESSAGE));
        }
    }
}

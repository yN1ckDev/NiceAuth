package it.skrahs.niceauth.commands;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import it.skrahs.niceauth.NiceAuth;
import it.skrahs.niceauth.cache.ConfigCache;
import it.skrahs.niceauth.utils.ChatUtils;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class LoginCommand implements CommandExecutor {

    private final NiceAuth plugin;
    private final MongoCollection<Document> collection;

    public LoginCommand(NiceAuth plugin) {
        this.plugin = plugin;
        this.collection = plugin.getMongoManager().getDatabase().getCollection("passwords");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatUtils.color(ConfigCache.LOGIN_CORRECT_USE));
            return true;
        }

        String inputPassword = args[0];


        Document playerData = collection.find(Filters.eq("uuid", player.getUniqueId().toString())).first();

        if (playerData == null) {
            player.sendMessage(ChatUtils.color(ConfigCache.NOT_REGISTERED));
            return true;
        }


        String storedPassword = playerData.getString("password");


        if (storedPassword != null && ChatUtils.decryptPassword(storedPassword).equals(inputPassword)) {
            player.sendMessage(ChatUtils.color(ConfigCache.LOGIN_SUCCESFUL));
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.JUMP);
            player.setWalkSpeed(0.2f);
            player.setFlySpeed(0.1f);


            plugin.getAuthManager().setLoginInProgress(player);
            plugin.getAuthManager().setLoggedIn(player);
        } else {
            player.sendMessage(ChatUtils.color(ConfigCache.WRONG_PASSWORD));
        }

        return true;
    }
}

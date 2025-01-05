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

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterCommand implements CommandExecutor {

    private final NiceAuth plugin;
    private final MongoCollection<Document> collection;

    public RegisterCommand(NiceAuth plugin) {
        this.plugin = plugin;
        this.collection = plugin.getMongoManager().getDatabase().getCollection("passwords");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) commandSender;

        if (args.length != 2 || !args[0].equals(args[1])) {
            player.sendMessage(ChatUtils.color(ConfigCache.REGISTER_CORRECT_USE));
            return true;
        }

        String password = args[0];
        String registrationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        if (collection.find(Filters.eq("uuid", player.getUniqueId().toString())).first() != null) {
            player.sendMessage(ChatUtils.color(ConfigCache.ALREADY_REGISTERED));
            return true;
        }

        Document document = new Document("uuid", player.getUniqueId().toString())
                .append("password", ChatUtils.encryptPassword(password))
                .append("registrationDate", registrationDate);

        collection.insertOne(document);

        player.sendMessage(ChatUtils.color(ConfigCache.REGISTER_SUCCESFUL));
        player.kickPlayer(ChatUtils.color(ConfigCache.REGISTER_KICK_MESSAGE));
        return true;
    }
}

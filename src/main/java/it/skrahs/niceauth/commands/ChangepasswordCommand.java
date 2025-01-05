package it.skrahs.niceauth.commands;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import it.skrahs.niceauth.NiceAuth;
import it.skrahs.niceauth.cache.ConfigCache;
import it.skrahs.niceauth.utils.ChatUtils;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangepasswordCommand implements CommandExecutor {

    private final NiceAuth plugin;
    private final MongoCollection<Document> collection;

    public ChangepasswordCommand(NiceAuth plugin) {
        this.plugin = plugin;
        this.collection = plugin.getMongoManager().getDatabase().getCollection("passwords");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (args.length < 2) {
                player.sendMessage(ChatUtils.color(ConfigCache.CHANGEPASS_CORRECTUSE));
                return true;
            }

            String currentPassword = args[0];
            String newPassword = args[1];


            Document playerData = collection.find(Filters.eq("uuid", player.getUniqueId().toString())).first();

            if (playerData == null) {
                player.sendMessage(ChatUtils.color(ConfigCache.NOT_REGISTERED));
                return true;
            }


            String storedPassword = playerData.getString("password");


            if (!ChatUtils.decryptPassword(storedPassword).equals(currentPassword)) {
                player.sendMessage(ChatUtils.color(ConfigCache.WRONG_PASSWORD));
                return true;
            }

            collection.updateOne(
                    Filters.eq("uuid", player.getUniqueId().toString()),
                    Updates.set("password", ChatUtils.encryptPassword(newPassword))
            );

            player.sendMessage(ChatUtils.color(ConfigCache.CHANGEPASS_SUCCESSFULLY));
        } else {
            commandSender.sendMessage(ChatUtils.color("&cThis command can only be executed by a player."));
        }
        return true;
    }
}

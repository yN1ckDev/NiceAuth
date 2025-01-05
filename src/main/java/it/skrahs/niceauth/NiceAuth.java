package it.skrahs.niceauth;

import it.skrahs.niceauth.backend.MongoManager;
import it.skrahs.niceauth.cache.ConfigCache;
import it.skrahs.niceauth.commands.AuthCommand;
import it.skrahs.niceauth.commands.ChangepasswordCommand;
import it.skrahs.niceauth.commands.LoginCommand;
import it.skrahs.niceauth.commands.RegisterCommand;
import it.skrahs.niceauth.listener.CommandListener;
import it.skrahs.niceauth.listener.JoinListener;
import it.skrahs.niceauth.listener.QuitListener;
import it.skrahs.niceauth.managers.AuthManager;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public final class NiceAuth extends JavaPlugin {

    @Getter
    private static Plugin instance;

    private AuthManager authManager;

    private MongoManager mongoManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadCache();

        mongoManager = new MongoManager(this);
        authManager = new AuthManager(mongoManager.getDatabase());

        loadCommands();
        loadListeners();

        getLogger().info("  _   _ _                        _   _     \n" +
                "  | \\ | (_)            /\\        | | | |    \n" +
                "  |  \\| |_  ___ ___   /  \\  _   _| |_| |__  \n" +
                "  | . ` | |/ __/ _ \\ / /\\ \\| | | | __| '_ \\ \n" +
                "  | |\\  | | (_|  __// ____ \\ |_| | |_| | | |\n" +
                "  |_| \\_|_|\\___\\___/_/    \\_\\__,_|\\__|_| |_|\n" +
                "                                           \n" +
                "  Plugin was started Succesfully           \n" +
                "  Version: 1.0                             \n" +
                "  Author: Skrahs, AyoMattiol (@onEnable)                          \n" +
                "\n");
    }

    private void loadCommands() {
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("auth").setExecutor(new AuthCommand());
        getCommand("changepassword").setExecutor(new ChangepasswordCommand(this));
    }

    private void loadListeners() {
        PluginManager pm = getServer().getPluginManager();

        List.of(
                new JoinListener(this),
                new CommandListener(this),
                new QuitListener(this)
        ).forEach(listener -> pm.registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        mongoManager.close();

        getLogger().info("  _   _ _                        _   _     \n" +
                "  | \\ | (_)            /\\        | | | |    \n" +
                "  |  \\| |_  ___ ___   /  \\  _   _| |_| |__  \n" +
                "  | . ` | |/ __/ _ \\ / /\\ \\| | | | __| '_ \\ \n" +
                "  | |\\  | | (_|  __// ____ \\ |_| | |_| | | |\n" +
                "  |_| \\_|_|\\___\\___/_/    \\_\\__,_|\\__|_| |_|\n" +
                "                                           \n" +
                "  Plugin was disabled Succesfully           \n" +
                "  Version: 1.0                             \n" +
                "  Author: Skrahs" +
                "  Fork by: AyoMattiol (@onEnable)                           \n" +
                "\n");

    }

    public void loadCache() {
        ConfigCache.load();
    }

}

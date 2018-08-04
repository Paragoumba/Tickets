package fr.paragoumba.minediversity.tickets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Created by Paragoumba on 12/06/2017.
 */

public class Tickets extends JavaPlugin {

    static HashMap<String, String> args = new HashMap<>();
    static Tickets plugin;
    static ChatColor mainColor;
    static ChatColor errorColor;

    @Override
    public void onEnable() {

        Bukkit.getLogger().log(Level.INFO, "Enabling Tickets.");

        plugin = this;
        File config = new File(getDataFolder(), "config.yml");

        try {

            if (!config.exists()) {

                getConfig().options().copyDefaults(true);
                saveConfig();

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        args.put("create", "/ticket create <raison>");
        args.put("list", "/ticket list");
        args.put("take", "/ticket take <id>");
        args.put("tp", "/ticket tp <id>");
        args.put("close", "/ticket close <id>");
        args.put("reload", "/ticket reload");
        args.put("reset", "/ticket reset");

        //Commands
        getCommand("ticket").setExecutor(new TicketCommand());

    }

    @Override
    public void onDisable() {

        Bukkit.getLogger().log(Level.INFO, "Disabling Tickets.");

    }

    static void init(){

        Configuration configuration = plugin.getConfig();
        mainColor = ChatColor.valueOf(configuration.getString("mainColor"));
        errorColor = ChatColor.valueOf(configuration.getString("errorColor"));
        Database.init();

    }
}

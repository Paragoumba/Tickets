package fr.paragoumba.tickets;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Paragoumba on 12/06/2017.
 */

public class Tickets extends JavaPlugin {
    public static Tickets plugin;

    @Override
    public void onEnable() {
        super.onEnable();

        File config = new File(getDataFolder(), "config.yml");

        try {
            if (!config.exists()) {

                getConfig().options().copyDefaults(true);
                saveConfig();
                saveDefaultConfig();

            }
        } catch (Exception e) {

            e.printStackTrace();

        }

        plugin = this;

        DB.connect();

        getCommand("ticket").setExecutor(new TicketCommand());
    }

    @Override
    public void onDisable() {
        super.onDisable();

        DB.disconnect();

    }
}

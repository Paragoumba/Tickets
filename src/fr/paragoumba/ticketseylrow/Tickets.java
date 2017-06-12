package fr.paragoumba.ticketseylrow;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Created by Paragoumba on 12/06/2017.
 */

public class Tickets extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();

        Bukkit.getLogger().log(Level.WARNING, "Test");
    }
}

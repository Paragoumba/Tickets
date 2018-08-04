package fr.paragoumba.minediversity.tickets;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Paragoumba on 13/06/2017.
 */

class Ticket {

    Ticket(int id, Player player, String message, Location loc, Player helper){

        this.id = id;
        this.player = player;
        this.message = message;
        this.loc = loc;
        this.helper = helper;

    }

    private int id;
    private Player player;
    private String message;
    private Location loc;
    private Player helper;

    int getID(){

        return id;

    }

    Player getPlayer(){

        return player;

    }

    String getMessage(){

        return message;

    }

    Location getLoc(){

        return loc;

    }

    boolean hasHelper(){

        return helper != null;

    }

    void setHelper(Player helper){

        this.helper = helper;

    }

    Player getHelper(){

        return helper;

    }
}

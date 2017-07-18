package fr.paragoumba.ticketseylrow;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import static fr.paragoumba.ticketseylrow.Tickets.plugin;

/**
 * Created by Paragoumba on 13/06/2017.
 */

class DB {

    private DB(){}

    private static Configuration config = plugin.getConfig();
    private static Connection connection;
    private static String url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/" + config.getString("database");
    private static String login = config.getString("login");
    private static String password = config.getString("password");

    static ArrayList<Ticket> getTickets(){

        try(Statement state = connection.createStatement();
            ResultSet result = state.executeQuery("SELECT * FROM Tickets")){

            ArrayList<Ticket> tickets = new ArrayList<>();
            int id = 0;
            Player player = null;
            String message = "";
            Location loc = null;
            Player helper = null;

            while (result.next()){

                for (int i = 1; i <= result.getMetaData().getColumnCount(); ++i) {

                    id = result.getInt("id");

                    player = Bukkit.getPlayer(UUID.fromString(result.getString("player")));

                    message = result.getString("message");

                    String[] coos = result.getString("location").split(",");
                    loc = new Location(Bukkit.getWorld(UUID.fromString(coos[0])), Double.parseDouble(coos[1]), Double.parseDouble(coos[2]), Double.parseDouble(coos[3]));

                    helper = result.getString("helper") != null ? Bukkit.getPlayer(UUID.fromString(result.getString("helper"))) : null;
                }

                tickets.add(new Ticket(id, player, message, loc, helper));

            }

            System.out.println("Creating Ticket");

            return tickets;

        } catch (Exception e) {

            System.out.println("Ticket error");
            e.printStackTrace();

        }

        System.out.println("Returning error");
        return null;
    }

    static Ticket getTicket(int id){

        try(Statement state = connection.createStatement();
            ResultSet result = state.executeQuery("SELECT * FROM Tickets")){

            Player player = null;
            String message = "";
            Location loc = null;
            Player helper = null;

            while (result.next()) {

                id = result.getInt("id");

                player = Bukkit.getPlayer(UUID.fromString(result.getString("player")));

                message = result.getString("message");

                String[] coos = result.getString("location").split(",");
                loc = new Location(Bukkit.getWorld(UUID.fromString(coos[0])), Double.parseDouble(coos[1]), Double.parseDouble(coos[2]), Double.parseDouble(coos[3]));

                helper = result.getString("helper") != null ? Bukkit.getPlayer(UUID.fromString(result.getString("helper"))) : null;

            }

            System.out.println("Creating Ticket");

            return new Ticket(id, player, message, loc, helper);

        } catch (Exception e) {

            System.out.println("Ticket error");
            e.printStackTrace();

        }

        System.out.println("Returning error");
        return null;
    }

    static void createTicket(Player player, String message, Location loc){

        try(Statement state = connection.createStatement()){

            state.executeUpdate("INSERT INTO Tickets VALUES (" + getLastID() + 1 + ", '" + player.getUniqueId() + "', '" + message + "', '" + loc.getWorld().getUID() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "', NULL);");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    static void removeTicket(int id){
        try(Statement state = connection.createStatement()){

            state.executeUpdate("DELETE FROM Tickets WHERE id = " + id);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    static int getLastID(){

        try(Statement state = connection.createStatement();
            ResultSet result = state.executeQuery("SELECT max(id) FROM Tickets")){

            return result.getInt("id");

        } catch (Exception e) {

            System.out.println("Error in id getting");
            e.printStackTrace();

        }

        return 0;

    }

    static void connect() {

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, login, password);
            Bukkit.getLogger().log(Level.WARNING, "Tickets: Database connected. (" + url + ")");

        } catch (Exception e) {

            Bukkit.getLogger().log(Level.WARNING, "Tickets: Error in database connection. (" + url + ")");
            e.printStackTrace();

        }
    }

    static void disconnect() {

        if (connection != null) {

            try {

                connection.close();
                Bukkit.getLogger().log(Level.WARNING, "Tickets: Database disconnected.");

            } catch (SQLException e) {

                Bukkit.getLogger().log(Level.WARNING, "Tickets: Error in database disconnection.");
                e.printStackTrace();

            }

        }
    }

    static void reset(){

        try(Statement state = connection.createStatement()){

            state.executeUpdate("DELETE FROM Tickets");

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }
}

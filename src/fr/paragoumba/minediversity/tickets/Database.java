package fr.paragoumba.minediversity.tickets;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class handles all incoming and outgoing data from the database.
 */

class Database {

    private Database(){}

    private static String database;
    private static String url;
    private static String login;
    private static String password;
    private static String table;

    /**
     * Saves a Ticket
     *
     * @param player The player who created the {@code Ticket}
     * @param message The message of the {@code Ticket}
     * @param loc The location where the {@code Ticket} has been created
     * @see Ticket
     */
    static void createTicket(Player player, String message, Location loc){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            Statement state = connection.createStatement()){

            state.executeUpdate("INSERT INTO `" + table +"` VALUES (" + (getLastID() + 1) + ", '" + player.getUniqueId() + "', '" + player.getName() + "', '" + message + "', '" + loc.getWorld().getUID() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "', NULL);");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    static ArrayList<Ticket> getTickets(){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            Statement state = connection.createStatement();
            ResultSet result = state.executeQuery("SELECT * FROM `" + table + "`")){

            ArrayList<Ticket> tickets = new ArrayList<>();

            while (result.next()){

                String[] coos = result.getString("location").split(",");

                Player helper = result.getString("helper") != null ? Bukkit.getPlayer(UUID.fromString(result.getString("helper"))) : null;
                tickets.add(new Ticket(result.getInt("id"), Bukkit.getPlayer(UUID.fromString(result.getString("player"))), result.getString("message"), new Location(Bukkit.getWorld(UUID.fromString(coos[0])), Double.parseDouble(coos[1]), Double.parseDouble(coos[2]), Double.parseDouble(coos[3])), helper));

            }

            return tickets;

        } catch (Exception e) {

            Bukkit.getLogger().log(Level.SEVERE, "Ticket's error.");
            e.printStackTrace();

        }

        return null;
    }

    static Ticket getTicket(int id){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            Statement state = connection.createStatement();
            ResultSet result = state.executeQuery("SELECT * FROM `" + table + "` WHERE id = " + id)){

            result.next();
            String[] coos = result.getString("location").split(",");
            return new Ticket(result.getInt("id"), Bukkit.getPlayer(UUID.fromString(result.getString("player"))), result.getString("message"), new Location(Bukkit.getWorld(UUID.fromString(coos[0])), Double.parseDouble(coos[1]), Double.parseDouble(coos[2]), Double.parseDouble(coos[3])), result.getString("helper") != null ? Bukkit.getPlayer(UUID.fromString(result.getString("helper"))) : null);

        } catch (Exception e) {

            Bukkit.getLogger().log(Level.SEVERE, "Ticket's error.");
            e.printStackTrace();

        }
        return null;
    }

    static void removeTicket(int id){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            Statement state = connection.createStatement()){

            state.executeUpdate("DELETE FROM `" + table + "` WHERE id = " + id);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    static int getLastID(){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            Statement state = connection.createStatement();
            ResultSet result = state.executeQuery("SELECT max(id) FROM `" + table + "`")){

            int lastId = 0;

            while (result.next()){

                lastId = result.getInt(1);

            }

            return lastId;

        } catch (Exception e) {

            Bukkit.getLogger().log(Level.WARNING, "Error in getting last id.");
            e.printStackTrace();

        }

        return 0;

    }

    /**Gets in the database the latest known pseudo of the {@code Ticket}'s creator.
     *
     * @param id The id of a ticket
     * @return Returns the pseudo in string form
     */
    static String getLastPseudo(int id){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            Statement state = connection.createStatement();
            ResultSet result = state.executeQuery("SELECT lastPseudo FROM `" + table + "` WHERE id = " + id)){

            String lastPseudo = "NULL";

            while (result.next()){

                lastPseudo = result.getString(1);

            }

            return lastPseudo;

        } catch (Exception e) {

            Bukkit.getLogger().log(Level.SEVERE, "Error in getting last pseudo.");
            e.printStackTrace();

        }

        return "NULL";
    }

    /**Set the helper of the ticket
     *
     * @param id The id of a ticket
     * @param uuid The UUID of a player
     * @return true if the operation succeeded else false
     */
    static boolean setHelper(int id, UUID uuid){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            Statement state = connection.createStatement()){

            String uniqueId = uuid != null ? '\'' + uuid.toString() + '\'' : null;

            state.executeUpdate("UPDATE `" + table + "` SET helper=" + uniqueId + " WHERE id=" + id);

            return true;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return false;
    }

    static void init(){

        Configuration config = Tickets.plugin.getConfig();
        database = config.getString("database");
        url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/" + database;
        login = config.getString("login");
        password = config.getString("password");
        table = config.getString("table");

        try(Connection connection = DriverManager.getConnection(url, login, password)){

            try(Statement state = connection.createStatement()){

                state.executeQuery("SELECT max(id) FROM `" + table + "`");

            } catch (MySQLSyntaxErrorException e){

                Statement state = connection.createStatement();
                state.executeUpdate("CREATE TABLE " + database + ".`" + table + "` (" +
                        "id SMALLINT(5)," +
                        "player TINYTEXT," +
                        "lastPseudo TINYTEXT," +
                        "message TEXT," +
                        "location TINYTEXT," +
                        "helper TINYTEXT" +
                        ") ENGINE = INNODB");

                state.close();
            }

            Bukkit.getLogger().log(Level.FINE, "Tickets: Database works. (" + url + ")");

        } catch (SQLException e) {

            Bukkit.getLogger().log(Level.SEVERE, "Tickets: Error in database connection. (" + url + ")");
            e.printStackTrace();

        }
    }

    static void reset(){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            Statement state = connection.createStatement()){

            state.executeUpdate("DELETE FROM `" + table + "`");

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }
}

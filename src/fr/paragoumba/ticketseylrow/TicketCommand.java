package fr.paragoumba.ticketseylrow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by Paragoumba on 12/06/2017.
 */

public class TicketCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player){

            Player player = (Player) commandSender;

            if (strings.length > 0) {

                if (strings[0].equalsIgnoreCase("modolist")) {

                    ArrayList<Ticket> tickets = DB.getTickets();

                    if (tickets != null) {

                        player.sendMessage("Tickets:");

                        for (Ticket ticket : tickets) {

                            player.sendMessage("   -" + ChatColor.GOLD + (ticket.getPlayer() != null ? ticket.getPlayer().getDisplayName() : "NULL") + ChatColor.WHITE + " (#" + ticket.getID() + ") :");
                            player.sendMessage("    " + (ticket.getMessage() != null ? ticket.getMessage() : "NULL"));

                        }

                    }

                    return true;

                } else if (strings[0].equalsIgnoreCase("adminreload")) {

                    DB.disconnect();
                    DB.connect();
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Tickets:" + ChatColor.WHITE + " Tickets reloaded.");

                    return true;

                } else if (strings[0].equalsIgnoreCase("adminreset")) {

                    DB.reset();
                    player.sendMessage("The database has been reset.");

                    return true;

                } else if (strings[0].equalsIgnoreCase("create")) {

                    StringBuilder message = new StringBuilder();

                    for (String arg : strings) {

                        message.append(!message.toString().equals("") ? " " : "");
                        message.append(!arg.equals("create") ? arg.replaceAll("\'", "''").replaceAll("\"", "\"\"") : "");

                    }

                    DB.createTicket(player, message.toString(), player.getLocation());
                    player.sendMessage("Ticket envoyé.");

                    return true;

                } else if (strings[0].equalsIgnoreCase("modo")) {

                    try {

                        int id = Integer.parseInt(strings[1]);
                        Ticket ticket = DB.getTicket(id);

                        if (ticket != null) {

                            ticket.setHelper(player);
                            player.sendMessage("Vous vous occupez désormais du ticket #" + id);

                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                        player.sendMessage("Invalid id's number.");

                    }

                    return true;

                } else if (strings[0].equalsIgnoreCase("modotp")) {

                    try {

                        int id = Integer.parseInt(strings[1]);
                        Ticket ticket = DB.getTicket(id);

                        if (ticket != null) {

                            player.teleport(ticket.getLoc());

                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                        player.sendMessage("Invalid id's number.");

                    }

                    return true;

                } else if (strings[0].equalsIgnoreCase("modoclose")) {

                    try {

                        int id = Integer.parseInt(strings[1]);

                        DB.removeTicket(id);
                        player.sendMessage("Ticket #" + id + " résolu.");

                    } catch (Exception e) {

                        e.printStackTrace();
                        player.sendMessage("Invalid id's number.");

                    }

                    return true;

                }

                return false;

            } else {

                return false;

            }
        }
        return true;
    }
}

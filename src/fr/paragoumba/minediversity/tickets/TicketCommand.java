package fr.paragoumba.minediversity.tickets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static fr.paragoumba.minediversity.tickets.Tickets.*;

/**
 * Created by Paragoumba on 12/06/2017.
 */

public class TicketCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player){

            Player player = (Player) commandSender;

            if (strings.length > 0) {

                switch (strings[0]) {

                    case "create":

                        if (strings.length > 1) {

                            StringBuilder message = new StringBuilder();

                            for (String arg : strings) {

                                message.append(!message.toString().equals("") ? " " : "");
                                message.append(!arg.equals("create") ? arg.replaceAll("\'", "''").replaceAll("\"", "\"\"") : "");

                            }

                            Database.createTicket(player, message.toString(), player.getLocation());
                            player.sendMessage("Ticket envoyé.");
                            Bukkit.broadcast("Un ticket a été créé par " + mainColor + player.getDisplayName() + ChatColor.RESET + ".", "tickets.modo");

                            return true;

                        }

                        player.sendMessage(args.get(strings[0]));
                        return true;

                    case "read":

                        if (player.hasPermission("tickets.modo")) {

                            if (strings.length > 1) {

                                try {

                                    int id = Integer.parseInt(strings[1]);
                                    Ticket ticket = Database.getTicket(id);

                                    if (ticket != null) {

                                        Player ticketCreator = ticket.getPlayer();

                                        player.sendMessage("Ticket #" + id + " par " + ChatColor.GOLD + (ticketCreator != null ? ticketCreator.getName() : Database.getLastPseudo(id)) + ChatColor.RESET + ":");
                                        player.sendMessage("  " + ticket.getMessage());

                                        return true;

                                    }

                                    player.sendMessage("Ticket corrompu.");
                                    return true;

                                } catch (NumberFormatException e) {

                                    e.printStackTrace();

                                }
                            }
                        }

                        invalidId(player);
                        return true;

                    case "list":

                        if (player.hasPermission("tickets.modo")) {

                            ArrayList<Ticket> tickets = Database.getTickets();

                            if (tickets != null && !tickets.isEmpty()) {

                                player.sendMessage("Tickets:");

                                for (Ticket ticket : tickets) {

                                    char bracket = '[';
                                    UUID playerUUID = player.getUniqueId();
                                    Player ticketCreator = ticket.getPlayer();
                                    Player helper = ticket.getHelper();
                                    String message = ticket.getMessage();

                                    if (helper != null && helper.getUniqueId().equals(playerUUID)) bracket = '(';

                                    player.sendMessage("   - " + bracket + (ticket.hasHelper() ? ChatColor.RED : ChatColor.GREEN) + ticket.getID() + ChatColor.RESET + (bracket == '(' ? ')' : ']') + ' ' + (ticketCreator != null ? ticketCreator.getDisplayName() : Database.getLastPseudo(ticket.getID())) + " : " + (message.length() > 25 ? message.substring(0, 25) : message));

                                }

                            } else {

                                player.sendMessage("Aucun ticket.");

                            }

                            return true;
                        }

                        accessError(player);
                        return true;

                    case "take":

                        if (player.hasPermission("tickets.modo")) {

                            if (strings.length > 1) {

                                try {

                                    int id = Integer.parseInt(strings[1]);

                                    if (Database.setHelper(id, player.getUniqueId())) {

                                        player.sendMessage("Vous vous occupez désormais du ticket #" + id + ".");

                                    }

                                    return true;

                                } catch (NumberFormatException e) {

                                    e.printStackTrace();

                                }
                            }

                            invalidId(player);
                            player.sendMessage(args.get(strings[0]));
                            return true;

                        }

                        accessError(player);
                        return true;

                    case "free":

                        if (player.hasPermission("tickets.modo")) {

                            if (strings.length > 1) {

                                try {

                                    int id = Integer.parseInt(strings[1]);

                                    if (Database.setHelper(id, null)) {

                                        player.sendMessage("Vous ne vous occupez plus du ticket #" + id + ".");

                                    }

                                    return true;

                                } catch (NumberFormatException e) {

                                    e.printStackTrace();

                                }
                            }

                            invalidId(player);
                            player.sendMessage(args.get(strings[0]));
                            return true;
                        }

                        accessError(player);
                        return true;

                    case "tp":

                        if (player.hasPermission("tickets.modo")) {

                            if (strings.length > 1) {

                                try {

                                    Ticket ticket = Database.getTicket(Integer.parseInt(strings[1]));

                                    if (ticket != null) {

                                        player.teleport(ticket.getLoc());

                                    }

                                    return true;

                                } catch (NumberFormatException e) {

                                    e.printStackTrace();

                                }
                            }

                            invalidId(player);
                            player.sendMessage(args.get(strings[0]));
                            return true;
                        }

                        accessError(player);
                        return true;

                    case "close":

                        if (player.hasPermission("tickets.modo")) {

                            if (strings.length > 1) {

                                try {

                                    int id = Integer.parseInt(strings[1]);

                                    Database.removeTicket(id);
                                    player.sendMessage("Ticket #" + id + " " + ChatColor.GREEN + "résolu" + ChatColor.RESET + ".");

                                    return true;

                                } catch (NumberFormatException e) {

                                    e.printStackTrace();

                                }
                            }

                            invalidId(player);
                            player.sendMessage(args.get(strings[0]));
                            return true;
                        }

                        accessError(player);
                        return true;

                    case "reload":

                        if (player.hasPermission("tickets.*")) {

                            init();
                            Bukkit.broadcast(mainColor + "Tickets:" + ChatColor.RESET + " Tickets reloaded.", "tickets.modo");

                            return true;

                        }

                        accessError(player);
                        return true;

                    case "reset":

                        if (player.hasPermission("tickets.*")) {

                            Database.reset();
                            player.sendMessage("The database has been reset.");

                            return true;

                        }

                        accessError(player);
                        return true;
                }
            }

            player.sendMessage("-§6[§f|§6]§f-------------------§6Tickets' Help§f------------------§6[§f|§6]§f-\n" +
                    "/ticket create <raison> : Création d'un ticket.");

            if (player.hasPermission("tickets.modo")) {

                player.sendMessage("/ticket list : Voir les tickets en cours.\n" +
                        "/ticket take <id> : Se positionner sur le ticket souhaité.\n" +
                        "/ticket tp <id> : Se TP aux positions du ticket en question.\n" +
                        "/ticket close <id> : Fermer un ticket.");

                if (player.hasPermission("tickets.*")) {

                    player.sendMessage("/ticket reload : Redémarrer du plugin.\n" +
                            "/ticket reset : Supprimer tous les tickets.");
                }
            }

            player.sendMessage("-§6[§f|§6]§f-------------------§6Tickets' Help§f------------------§6[§f|§6]§f-");
            return true;
        }

        commandSender.sendMessage("Only players can use this command. ¯\\_(ツ)_/¯");
        return true;
    }

    private void accessError(Player player){

        player.sendMessage(errorColor + "Vous n'avez pas accès à cette commande.");

    }

    private void invalidId(Player player){

        player.sendMessage(errorColor + "L'id entré est invalide.");

    }
}

package me.ivehydra.commandsmanager.commands;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandsManagerCommands implements CommandExecutor {

    private final CommandsManager instance = CommandsManager.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("commandsmanager")) {
            if(!(sender instanceof Player)) {
                switch(args.length) {
                    case 0:
                        sendHelp(sender);
                        break;
                    case 1:
                        if(args[0].equalsIgnoreCase("help")) {
                            sendHelp(sender);
                        } else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                            instance.reloadConfigFile();
                            sender.sendMessage(MessageUtils.CONFIG_RELOADED.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        } else {
                            sender.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                            return true;
                        }
                        break;
                    default:
                        sender.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        break;
                }
                return true;
            }
            Player p = (Player) sender;
            switch(args.length) {
                case 0:
                    if(!p.hasPermission("commandsmanager.help")) {
                        sendNoHelp(p);
                        return true;
                    }
                    sendHelp(p);
                    break;
                case 1:
                    if(args[0].equalsIgnoreCase("help")) {
                        if(!p.hasPermission("commandsmanager.help")) {
                            sendNoHelp(p);
                            return true;
                        }
                        sendHelp(p);
                    } else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                        if(!p.hasPermission("commandsmanager.reload")) {
                            p.sendMessage(MessageUtils.NO_PERMISSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                            return true;
                        }
                        instance.reloadConfigFile();
                        p.sendMessage(MessageUtils.CONFIG_RELOADED.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                    } else {
                        p.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        return true;
                    }
                    break;
                default:
                    p.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                    break;
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.RED + "CommandsManager by " + ChatColor.YELLOW + "IVEHydra" + ChatColor.GRAY + " v" + ChatColor.RED + instance.getDescription().getVersion() + ChatColor.GRAY + " -------");
        sender.sendMessage(ChatColor.RED + "Commands:");
        sender.sendMessage(ChatColor.RED + "/commandsmanager help" + ChatColor.GRAY + " - Sends a message with all commands and permissions.");
        sender.sendMessage(ChatColor.RED + "/commandsmanager reload | rl" + ChatColor.GRAY + " - Reloads the configuration file.");
        sender.sendMessage(ChatColor.RED + "Permissions:");
        sender.sendMessage(ChatColor.RED + "commandsmanager.*" + ChatColor.GRAY + " - Allows to execute all commands.");
        sender.sendMessage(ChatColor.RED + "commandsmanager.help" + ChatColor.GRAY + " - Allows to see all commands and permissions.");
        sender.sendMessage(ChatColor.RED + "commandsmanager.reload" + ChatColor.GRAY + " - Allows to reload the configuration file.");
        sender.sendMessage(ChatColor.RED + "commandsmanager.bypass.blockColons" + ChatColor.GRAY + " - Allows to execute commands with colons(:) in the first argument of the command.");
        sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.RED + "CommandsManager by " + ChatColor.YELLOW + "IVEHydra" + ChatColor.GRAY + " v" + ChatColor.RED + instance.getDescription().getVersion() + ChatColor.GRAY + " -------");
    }

    private void sendNoHelp(CommandSender sender) { sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.RED + "CommandsManager by " + ChatColor.YELLOW + "IVEHydra" + ChatColor.GRAY + " v" + ChatColor.RED + instance.getDescription().getVersion() + ChatColor.GRAY + " -------"); }

}

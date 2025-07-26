package me.ivehydra.commandsmanager.commands;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.cooldown.Cooldown;
import me.ivehydra.commandsmanager.cooldown.CooldownManager;
import me.ivehydra.commandsmanager.cooldown.PlayerCooldown;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CommandsManagerCommands implements CommandExecutor {

    private final CommandsManager instance = CommandsManager.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(cmd.getName().equalsIgnoreCase("commandsmanager")) {

            boolean isPlayer = sender instanceof Player;
            Player p = isPlayer ? (Player) sender : null;

            if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
                if(isPlayer && !p.hasPermission("commandsmanager.help"))
                    sendNoHelp(p);
                else
                    sendHelp(sender);
                return true;
            }

            if(args.length == 1 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))) {
                if(isPlayer && !p.hasPermission("commandsmanager.reload")) {
                    p.sendMessage(MessageUtils.NO_PERMISSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                    return true;
                }
                instance.reloadConfigFile();
                sender.sendMessage(MessageUtils.CONFIG_RELOADED.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                return true;
            }

            if(args.length >= 3 && args[0].equalsIgnoreCase("reset")) {
                if(isPlayer && !p.hasPermission("commandsmanager.reset")) {
                    p.sendMessage(MessageUtils.NO_PERMISSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                    return true;
                }

                String name = args[1];
                String command = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                CooldownManager cooldownManager = instance.getCooldownManager();

                if(command.equalsIgnoreCase("all")) {
                    cooldownManager.removeAllCooldowns(name);
                    sender.sendMessage(MessageUtils.ALL_COOLDOWNS_REMOVED.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%player_name%", name));
                } else {
                    PlayerCooldown pc = cooldownManager.getPlayerFromName(name);
                    if(pc == null) {
                        sender.sendMessage(MessageUtils.NO_PLAYER.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%player_name%", name));
                        return true;
                    }
                    Cooldown cooldown = pc.getCooldownByCommand(command);
                    if(cooldown == null) {
                        sender.sendMessage(MessageUtils.NO_COOLDOWN.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%player_name%", name, "%command_name%", command));
                        return true;
                    }
                    cooldownManager.removeCooldown(name, command);
                    sender.sendMessage(MessageUtils.COOLDOWN_REMOVED.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%player_name%", name, "%command_name%", command));

                }
                return true;
            }
            sender.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
            return true;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.RED + "CommandsManager by " + ChatColor.YELLOW + "IVEHydra" + ChatColor.GRAY + " v" + ChatColor.RED + instance.getDescription().getVersion() + ChatColor.GRAY + " -------");
        sender.sendMessage(ChatColor.RED + "Commands:");
        sender.sendMessage(ChatColor.RED + "/commandsmanager help" + ChatColor.GRAY + " - Sends a message with all commands and permissions.");
        sender.sendMessage(ChatColor.RED + "/commandsmanager reload | rl" + ChatColor.GRAY + " - Reloads the configuration file.");
        sender.sendMessage(ChatColor.RED + "/commandsmanager reset <player_name> <all/command>" + ChatColor.GRAY + " - Resets a player's cooldown for all commands or a specific command.");
        sender.sendMessage(ChatColor.RED + "Permissions:");
        sender.sendMessage(ChatColor.RED + "commandsmanager.*" + ChatColor.GRAY + " - Allows to execute all commands.");
        sender.sendMessage(ChatColor.RED + "commandsmanager.help" + ChatColor.GRAY + " - Allows to see all commands and permissions.");
        sender.sendMessage(ChatColor.RED + "commandsmanager.reload" + ChatColor.GRAY + " - Allows to reload the configuration file.");
        sender.sendMessage(ChatColor.RED + "commandsmanager.reset" + ChatColor.GRAY + " - Allows to reset a player's cooldown for all commands or a specific command.");
        sender.sendMessage(ChatColor.RED + "commandsmanager.bypass.blockColons" + ChatColor.GRAY + " - Allows to execute commands with colons(:) in the first argument of the command.");
        sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.RED + "CommandsManager by " + ChatColor.YELLOW + "IVEHydra" + ChatColor.GRAY + " v" + ChatColor.RED + instance.getDescription().getVersion() + ChatColor.GRAY + " -------");
    }

    private void sendNoHelp(CommandSender sender) { sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.RED + "CommandsManager by " + ChatColor.YELLOW + "IVEHydra" + ChatColor.GRAY + " v" + ChatColor.RED + instance.getDescription().getVersion() + ChatColor.GRAY + " -------"); }

}

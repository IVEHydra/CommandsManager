package me.ivehydra.commandsmanager.commands;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.cooldown.Cooldown;
import me.ivehydra.commandsmanager.cooldown.PlayerCooldown;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandsManagerTabCompleter implements TabCompleter {

    private final CommandsManager instance = CommandsManager.getInstance();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("commandsmanager")) {
            List<String> argsList = new ArrayList<>();
            if(args.length == 1) {
                if(hasPermission(sender, "commandsmanager.help")) argsList.add("help");
                if(hasPermission(sender, "commandsmanager.reload"))  argsList.add("reload");
                if(hasPermission(sender, "commandsmanager.reset")) argsList.add("reset");
                return argsList.stream().filter(string -> string.startsWith(args[0])).collect(Collectors.toList());
            }

            if(args[0].equalsIgnoreCase("reset")) {
                if(!hasPermission(sender, "commandsmanager.reset"))
                    return Collections.emptyList();
            }

            if(args.length == 2 && args[0].equalsIgnoreCase("reset")) {
                for(Player po : Bukkit.getOnlinePlayers())
                    argsList.add(po.getName());
                return argsList.stream().filter(string -> string.startsWith(args[1])).collect(Collectors.toList());
            }

            if(args.length == 3 && args[0].equalsIgnoreCase("reset")) {
                String target = args[1];
                PlayerCooldown pc = instance.getCooldownManager().getPlayerFromName(target);

                argsList.add("all");
                if(pc != null) {
                    for(Cooldown cooldown : pc.getCooldowns())
                        argsList.add(cooldown.getCommand());
                }

                return argsList.stream().filter(string -> string.startsWith(args[2])).collect(Collectors.toList());
            }
            return argsList;
        }
        return Collections.emptyList();
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        if(!(sender instanceof Player)) return true;
        else return sender.hasPermission(permission);
    }

}

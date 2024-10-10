package me.ivehydra.commandsmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandsManagerTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("commandsmanager")) {
            List<String> argsList = new ArrayList<>();
            if(args.length == 1) {
                if(hasPermission(sender, "commandsmanager.help")) argsList.add("help");
                if(hasPermission(sender, "commandsmanager.reload"))  argsList.add("reload");
                return argsList.stream().filter(string -> string.startsWith(args[0])).collect(Collectors.toList());
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

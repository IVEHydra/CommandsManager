package me.ivehydra.commandsmanager.action.actions;

import me.ivehydra.commandsmanager.action.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandConsoleAction implements Action {

    @Override
    public String getName() { return "COMMAND_CONSOLE"; }

    @Override
    public void execute(Player p, String string) { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string); }

}

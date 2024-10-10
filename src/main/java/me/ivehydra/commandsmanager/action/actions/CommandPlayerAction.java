package me.ivehydra.commandsmanager.action.actions;

import me.ivehydra.commandsmanager.action.Action;
import org.bukkit.entity.Player;

public class CommandPlayerAction implements Action {

    @Override
    public String getName() { return "COMMAND_PLAYER"; }

    @Override
    public void execute(Player p, String string) { p.performCommand(string); }

}

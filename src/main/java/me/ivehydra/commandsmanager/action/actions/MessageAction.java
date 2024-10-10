package me.ivehydra.commandsmanager.action.actions;

import me.ivehydra.commandsmanager.action.Action;
import org.bukkit.entity.Player;

public class MessageAction implements Action {

    @Override
    public String getName() { return "MESSAGE"; }

    @Override
    public void execute(Player p, String string) { p.sendMessage(string); }

}

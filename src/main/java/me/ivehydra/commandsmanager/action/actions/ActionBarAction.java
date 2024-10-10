package me.ivehydra.commandsmanager.action.actions;

import com.cryptomorin.xseries.messages.ActionBar;
import me.ivehydra.commandsmanager.action.Action;
import org.bukkit.entity.Player;

public class ActionBarAction implements Action {

    @Override
    public String getName() { return "ACTIONBAR"; }

    @Override
    public void execute(Player p, String string) { ActionBar.sendActionBar(p, string); }

}

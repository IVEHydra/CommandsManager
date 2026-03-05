package me.ivehydra.commandsmanager.action.actions;

import com.cryptomorin.xseries.messages.Titles;
import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.action.Action;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class TitleAction implements Action {

    private final CommandsManager instance = CommandsManager.getInstance();

    @Override
    public String getName() { return "TITLE"; }

    @Override
    public void execute(Player p, String string, Runnable next) {
        String[] args = string.split(";");

        if(args.length != 5) {
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Invalid arguments for the title!");
            return;
        }

        String title = args[0];
        String subTitle = args[1];
        int fadeIn;
        int stay;
        int fadeOut;

        try {
            fadeIn = Integer.parseInt(args[2]);
            stay = Integer.parseInt(args[3]);
            fadeOut = Integer.parseInt(args[4]);
        } catch(NumberFormatException e) {
            fadeIn = 1;
            stay = 2;
            fadeOut = 1;
        }

        Titles.sendTitle(p, fadeIn * 20, stay * 20, fadeOut * 20, title, subTitle);
        next.run();
    }

}

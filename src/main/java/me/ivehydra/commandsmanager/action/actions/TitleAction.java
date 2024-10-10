package me.ivehydra.commandsmanager.action.actions;

import com.cryptomorin.xseries.messages.Titles;
import me.ivehydra.commandsmanager.action.Action;
import org.bukkit.entity.Player;

public class TitleAction implements Action {

    @Override
    public String getName() { return "TITLE"; }

    @Override
    public void execute(Player p, String string) {
        String[] args = string.split(";");
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
    }

}

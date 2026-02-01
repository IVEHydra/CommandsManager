package me.ivehydra.commandsmanager.action.actions;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.action.Action;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitAction implements Action {

    private final CommandsManager instance = CommandsManager
            .getInstance();

    @Override
    public String getName() { return "WAIT"; }

    @Override
    public void execute(Player p, String string, Runnable next) {

        int seconds;

        try {
            seconds = Integer.parseInt(string.trim());
        } catch(NumberFormatException e) {
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Wrong number format: " + string);
            seconds = 1;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                next.run();
            }
        }.runTaskLater(instance, seconds * 20L);

    }

}

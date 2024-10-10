package me.ivehydra.commandsmanager.delay;

import com.cryptomorin.xseries.messages.ActionBar;
import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.Command;
import me.ivehydra.commandsmanager.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Delay {

    private static final CommandsManager instance = CommandsManager.getInstance();
    private static final List<Player> delay = instance.getDelay();
    private static final List<Player> delayFailed = instance.getDelayFailed();

    public static void delay(Player p, String eCommand, Command command) {
        if(delayFailed.contains(p)) return;
        if(!delay.contains(p)) delay.add(p);

        int time = command.getTime(p);
        int loadingBarLenght = command.getLoadingBarLenght(p);

        BukkitRunnable delayRunnable = delayRunnable(p, eCommand, command, time, loadingBarLenght);
        delayRunnable.runTaskTimer(instance, 0L, 20L);

        BukkitRunnable failRunnable = failRunnable(p, eCommand, command, delayRunnable);
        failRunnable.runTaskTimer(instance, 0L, 5L);
    }

    private static BukkitRunnable delayRunnable(Player p, String eCommand, Command command, int time, int loadingBarLenght) {
        return new BukkitRunnable() {
            int currentTime = 0;
            @Override
            public void run() {
                if(!delay.contains(p)) {
                    cancel();
                    return;
                }
                currentTime++;
                if(time > 0) ActionBar.sendActionBar(p, LoadingBar.getLoadingBar(currentTime, time, loadingBarLenght, StringUtils.getColoredString(instance.getConfig().getString("loadingBar.completedColor")), StringUtils.getColoredString(instance.getConfig().getString("loadingBar.notCompletedColor")), instance.getConfig().getString("loadingBar.symbol")));
                if(currentTime > time) {
                    executeCommands(p, eCommand, command);
                    cancel();
                }
            }
        };
    }

    private static BukkitRunnable failRunnable(Player p, String eCommand, Command command, BukkitRunnable runnable) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if(!delayFailed.contains(p)) return;
                ActionBar.sendActionBar(p, "");
                instance.getActionManager().execute(p, command.getActionsOnFail(), eCommand, command);
                delayFailed.remove(p);
                runnable.cancel();
                cancel();
            }
        };
    }

    private static void executeCommands(Player p, String eCommand, Command command) {
        ActionBar.sendActionBar(p, "");
        if(!command.can(p, command)) {
            command.withdraw(p, command, eCommand);
            return;
        }
        command.withdraw(p, command, eCommand);
        p.performCommand(eCommand.replace("/", ""));
        instance.getActionManager().execute(p, command.getActionsOnSuccess(), eCommand, command);
        delay.remove(p);
    }

}

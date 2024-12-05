package me.ivehydra.commandsmanager.delay;

import com.cryptomorin.xseries.messages.ActionBar;
import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.Command;
import me.ivehydra.commandsmanager.command.modules.DelayModule;
import me.ivehydra.commandsmanager.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class Delay {

    private static final CommandsManager instance = CommandsManager.getInstance();
    private static final Set<Player> delay = instance.getDelay();
    private static final Set<Player> delayFailed = instance.getDelayFailed();

    public static void delay(Player p, String eCommand, Command command, boolean withCooldown, String cooldown) {
        if(delayFailed.contains(p)) return;
        delay.add(p);

        BukkitRunnable delayRunnable = runnable(p, eCommand, command, withCooldown, cooldown);
        delayRunnable.runTaskTimer(instance, 0L, 20L);

    }

    private static BukkitRunnable runnable(Player p, String eCommand, Command command, boolean withCooldown, String cooldown) {
        return new BukkitRunnable() {
            int currentTime = 0;
            final DelayModule delayModule = command.getDelayModule();
            final int time = delayModule.getDelayTime(p);
            final int loadingBarLength = delayModule.getLoadingBarLength(p);
            @Override
            public void run() {
                if(delayFailed.contains(p)) {
                    handleFail(p, eCommand, command);
                    cancel();
                    return;
                }
                if(!delay.contains(p)) {
                    cancel();
                    return;
                }
                if(time > 0) ActionBar.sendActionBar(p, LoadingBar.getLoadingBar(currentTime, time, loadingBarLength, StringUtils.getColoredString(instance.getConfig().getString("loadingBar.completedColor")), StringUtils.getColoredString(instance.getConfig().getString("loadingBar.notCompletedColor")), instance.getConfig().getString("loadingBar.symbol")));
                if(currentTime == time) {
                    if(withCooldown)
                        handleDelay(p, eCommand, command, cooldown);
                    else
                        handleDelay(p, eCommand, command, "");
                    cancel();
                    return;
                }
                currentTime++;
            }
        };
    }

    private static void handleFail(Player p, String eCommand, Command command) {
        ActionBar.sendActionBar(p, "");
        DelayModule delayModule = command.getDelayModule();
        instance.getActionManager().execute(p, delayModule.getActionsOnFail(), eCommand, command);
        delayFailed.remove(p);
    }

    private static void handleDelay(Player p, String eCommand, Command command, String cooldown) {
        ActionBar.sendActionBar(p, "");
        DelayModule delayModule = command.getDelayModule();

        if(!handleCost(p, delayModule, eCommand)) {
            delay.remove(p);
            return;
        }

        p.performCommand(eCommand.replace("/", ""));
        instance.getActionManager().execute(p, delayModule.getActionsOnSuccess(), eCommand, command);

        if(!cooldown.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            instance.getCooldownManager().setCooldown(p, cooldown, currentTime);
        }

        delay.remove(p);
    }

    private static boolean handleCost(Player p, DelayModule delayModule, String eCommand) {
        if(delayModule.hasMoney(p, eCommand)) {
            delayModule.withdrawMoney(p);
            return true;
        } else return false;
    }

}

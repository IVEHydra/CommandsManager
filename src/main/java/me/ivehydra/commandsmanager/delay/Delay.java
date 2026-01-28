package me.ivehydra.commandsmanager.delay;

import com.cryptomorin.xseries.messages.ActionBar;
import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.Command;
import me.ivehydra.commandsmanager.command.modules.DelayModule;
import me.ivehydra.commandsmanager.manager.BossBarManager;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import me.ivehydra.commandsmanager.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Delay {

    private static final CommandsManager instance = CommandsManager.getInstance();
    private static final List<String> delay = instance.getDelay();
    private static final List<String> delayFailed = instance.getDelayFailed();

    public static void delay(String name, String eCommand, Command command, String cooldown) {
        if(delayFailed.contains(name)) return;
        delay.add(name);

        Player p = Bukkit.getPlayer(name);
        BukkitRunnable delayRunnable = runnable(p, eCommand, command, cooldown);
        delayRunnable.runTaskTimer(instance, 0L, 20L);

    }

    private static BukkitRunnable runnable(Player p, String eCommand, Command command, String cooldown) {
        return new BukkitRunnable() {
            int currentTime = 0;
            final DelayModule delayModule = command.getDelayModule();
            final int time = delayModule.getDelayTime(p);
            final int loadingBarLength = delayModule.getLoadingBarLength(p);
            final String name = p.getName();
            final boolean replace = instance.getConfig().getBoolean("loadingBar.bossBar.replaceActionBar");
            @Override
            public void run() {
                if(delayFailed.contains(name)) {
                    handleFail(p, eCommand, command);
                    cancel();
                    return;
                }
                if(!delay.contains(name)) {
                    cancel();
                    return;
                }
                if(time > 0) {
                    String bar = LoadingBar.getLoadingBar(currentTime, time, loadingBarLength, StringUtils.getColoredString(instance.getConfig().getString("loadingBar.completedColor")), StringUtils.getColoredString(instance.getConfig().getString("loadingBar.notCompletedColor")), instance.getConfig().getString("loadingBar.symbol"));
                    int missingTime = time - currentTime;
                    String message = MessageUtils.BOSSBAR.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_name%", eCommand, "%command_time_delay%", String.valueOf(missingTime), "%command_cost%", String.valueOf(delayModule.getCost(p)));
                    float percent = LoadingBar.getPercent(currentTime, time);

                    if(!replace)
                        ActionBar.sendActionBar(p, bar);
                    BossBarManager.show(p, message, percent);
                }
                if(currentTime == time) {
                    if(!cooldown.isEmpty())
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
        BossBarManager.remove(p);
        DelayModule delayModule = command.getDelayModule();
        String name = p.getName();
        instance.getActionManager().execute(p, delayModule.getActionsOnFail(), eCommand, command);
        delayFailed.remove(name);
    }

    private static void handleDelay(Player p, String eCommand, Command command, String cooldown) {
        ActionBar.sendActionBar(p, "");
        BossBarManager.remove(p);
        DelayModule delayModule = command.getDelayModule();
        String name = p.getName();

        if(!handleCost(p, delayModule, eCommand)) {
            delay.remove(name);
            return;
        }

        p.performCommand(eCommand.replace("/", ""));
        instance.getActionManager().execute(p, delayModule.getActionsOnSuccess(), eCommand, command);

        if(!cooldown.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            instance.getCooldownManager().setCooldown(p, cooldown, currentTime);
        }

        delay.remove(name);
    }

    private static boolean handleCost(Player p, DelayModule delayModule, String eCommand) {
        if(delayModule.hasMoney(p, eCommand)) {
            delayModule.withdrawMoney(p);
            return true;
        } else return false;
    }

}

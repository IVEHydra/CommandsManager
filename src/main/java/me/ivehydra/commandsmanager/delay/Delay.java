package me.ivehydra.commandsmanager.delay;

import com.cryptomorin.xseries.messages.ActionBar;
import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.Command;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import me.ivehydra.commandsmanager.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class Delay {

    private static final CommandsManager instance = CommandsManager.getInstance();
    private static final Set<Player> delay = instance.getDelay();
    private static final Set<Player> delayFailed = instance.getDelayFailed();

    public static void delay(Player p, String eCommand, Command command) {
        if(delayFailed.contains(p)) return;
        delay.add(p);

        BukkitRunnable delayRunnable = runnable(p, eCommand, command);
        delayRunnable.runTaskTimer(instance, 0L, 20L);

    }

    private static BukkitRunnable runnable(Player p, String eCommand, Command command) {
        return new BukkitRunnable() {
            int currentTime = 0;
            final int time = command.getTime(p);
            final int loadingBarLength = command.getLoadingBarLength(p);
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
                currentTime++;
                if(time > 0) ActionBar.sendActionBar(p, LoadingBar.getLoadingBar(currentTime, time, loadingBarLength, StringUtils.getColoredString(instance.getConfig().getString("loadingBar.completedColor")), StringUtils.getColoredString(instance.getConfig().getString("loadingBar.notCompletedColor")), instance.getConfig().getString("loadingBar.symbol")));
                if(currentTime > time) {
                    handleDelay(p, eCommand, command);
                    cancel();
                }
            }
        };
    }

    private static void handleFail(Player p, String eCommand, Command command) {
        ActionBar.sendActionBar(p, "");
        instance.getActionManager().execute(p, command.getActionsOnFail(), eCommand, command);
        delayFailed.remove(p);
    }

    private static void handleDelay(Player p, String eCommand, Command command) {
        ActionBar.sendActionBar(p, "");
        switch(command.getCostType()) {
            case EXPERIENCE:
                if(!command.hasEXP(p)) {
                    p.sendMessage(MessageUtils.NO_EXPERIENCE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_cost%", String.valueOf(command.getCost(p)), "%command_name%", eCommand));
                    delay.remove(p);
                    return;
                }
                command.withdrawEXP(p);
                break;
            case MONEY:
                if(!command.hasMoney(p)) {
                    p.sendMessage(MessageUtils.NO_MONEY.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_cost%", String.valueOf(command.getCost(p)), "%command_name%", eCommand));
                    delay.remove(p);
                    return;
                }
                instance.getEconomy().withdrawPlayer(p, command.getCost(p));
                break;
            case CUSTOM:
                if(!command.hasCustom(p)) {
                    p.sendMessage(MessageUtils.NO_CUSTOM.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_customMaterial%", command.getCustomMaterial().name(), "%command_cost%", String.valueOf(command.getCost(p)), "%command_name%", eCommand));
                    delay.remove(p);
                    return;
                }
                command.withdrawCustom(p);
                break;
        }
        p.performCommand(eCommand.replace("/", ""));
        instance.getActionManager().execute(p, command.getActionsOnSuccess(), eCommand, command);
        delay.remove(p);
    }

}

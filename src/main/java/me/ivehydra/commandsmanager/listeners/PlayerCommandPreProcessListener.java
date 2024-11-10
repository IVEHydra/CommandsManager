package me.ivehydra.commandsmanager.listeners;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.Command;
import me.ivehydra.commandsmanager.command.CommandManager;
import me.ivehydra.commandsmanager.command.CommandSettings;
import me.ivehydra.commandsmanager.command.modules.CooldownModule;
import me.ivehydra.commandsmanager.command.modules.DelayModule;
import me.ivehydra.commandsmanager.delay.Delay;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Set;

public class PlayerCommandPreProcessListener implements Listener {

    private final CommandsManager instance = CommandsManager.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreProcessEvent(PlayerCommandPreprocessEvent e) {
        if(e.isCancelled()) return;

        Player p = e.getPlayer();
        CommandManager commandManager = instance.getCommandManager();
        CommandSettings commandSettings = commandManager.getCommandSettings();
        Set<Player> delay = instance.getDelay();
        Set<Player> delayFailed = instance.getDelayFailed();

        if(commandSettings.isCommand() && delay.contains(p)) {
            e.setCancelled(true);
            delayFailed.add(p);
            delay.remove(p);
            return;
        }

        String eCommand = e.getMessage();

        if(commandSettings.isColons() && eCommand.split(" ")[0].contains(":") && !p.hasPermission("commandsmanager.bypass.blockColons")) {
            e.setCancelled(true);
            p.sendMessage(MessageUtils.BLOCK_COLONS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
            return;
        }

        for(Command command : commandManager.getCommands()) {
            if(command.isCommand(eCommand) && command.getWorlds().contains(p.getWorld())) {

                if(command.hasBlock()) {
                    if(command.hasPermission(p)) return;
                    e.setCancelled(true);
                    instance.getActionManager().execute(p, command.getBlockModule().getActions(), eCommand, command);
                    return;
                }

                if(command.hasCooldown() && command.hasDelay()) {
                    handleFirst(e, p, eCommand, command, delay);
                    return;
                }

                if(command.hasCooldown()) {
                    handleSecond(e, p, eCommand, command);
                    return;
                }

                if(command.hasDelay()) {
                    handleThird(e, p, eCommand, command, delay);
                    return;
                }
            }
        }
    }

    private void handleFirst(PlayerCommandPreprocessEvent e, Player p, String eCommand, Command command, Set<Player> delay) {
        CooldownModule cooldownModule = command.getCooldownModule();
        DelayModule delayModule = command.getDelayModule();
        if(command.hasPermission(p)) {
            if(handleCost(e, p, eCommand, delayModule)) {
                delayModule.withdrawMoney(p);
                instance.getActionManager().execute(p, delayModule.getActionsOnSuccess(), eCommand, command);
            }
        } else {
            command.getCommand(eCommand).ifPresent(string -> {
                String cooldown = instance.getCooldownManager().getFormattedCooldown(p, string, command);
                if(!cooldown.equals("0")) {
                    e.setCancelled(true);
                    instance.getActionManager().execute(p, cooldownModule.getActions(), string, command);
                } else {
                    if(handleCost(e, p, eCommand, delayModule)) {
                        e.setCancelled(true);
                        if(!delay.contains(p)) Delay.delay(p, eCommand, command, true, string);
                        instance.getActionManager().execute(p, delayModule.getActionsOnWait(), eCommand, command);
                    }
                }
            });
        }
    }

    private void handleSecond(PlayerCommandPreprocessEvent e, Player p, String eCommand, Command command) {
        if(command.hasPermission(p)) return;
        command.getCommand(eCommand).ifPresent(string -> {
            String cooldown = instance.getCooldownManager().getFormattedCooldown(p, string, command);
            CooldownModule cooldownModule = command.getCooldownModule();
            if(!cooldown.equals("0")) {
                e.setCancelled(true);
                instance.getActionManager().execute(p, cooldownModule.getActions(), string, command);
            } else {
                long time = System.currentTimeMillis();
                instance.getCooldownManager().setCooldown(p, string, time);
            }
        });
    }

    private void handleThird(PlayerCommandPreprocessEvent e, Player p, String eCommand, Command command, Set<Player> delay) {
        DelayModule delayModule = command.getDelayModule();
        if(command.hasPermission(p)) {
            if(handleCost(e, p, eCommand, delayModule)) {
                delayModule.withdrawMoney(p);
                instance.getActionManager().execute(p, delayModule.getActionsOnSuccess(), eCommand, command);
            }
        } else {
            if(handleCost(e, p, eCommand, delayModule)) {
                e.setCancelled(true);
                if(!delay.contains(p)) Delay.delay(p, eCommand, command, false, "");
                instance.getActionManager().execute(p, delayModule.getActionsOnWait(), eCommand, command);
            }
        }
    }

    private boolean handleCost(PlayerCommandPreprocessEvent e, Player p, String eCommand, DelayModule delayModule) {
        if(delayModule.hasMoney(p, eCommand))
            return true;
        else {
            e.setCancelled(true);
            return false;
        }
    }

}
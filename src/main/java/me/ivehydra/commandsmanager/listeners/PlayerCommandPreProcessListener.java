package me.ivehydra.commandsmanager.listeners;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.Command;
import me.ivehydra.commandsmanager.command.CommandManager;
import me.ivehydra.commandsmanager.command.CommandSettings;
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
                switch(command.getType()) {
                    case BLOCK:
                        if(p.hasPermission("commandsmanager.*") || p.hasPermission(command.getPermission())) return;
                        e.setCancelled(true);
                        instance.getActionManager().execute(p, command.getActions(), eCommand, command);
                        return;
                    case COOLDOWN:
                        if(p.hasPermission("commandsmanager.*") || p.hasPermission(command.getPermission())) return;
                        command.getCommand(eCommand).ifPresent(string -> {
                            String cooldown = instance.getCooldownManager().getFormattedCooldown(p, string, command);
                            if(!cooldown.equals("0")) {
                                e.setCancelled(true);
                                instance.getActionManager().execute(p, command.getActions(), string, command);
                            } else {
                                long currentTime = System.currentTimeMillis();
                                instance.getCooldownManager().setCooldown(p, string, currentTime);
                            }
                        });
                        return;
                    case DELAY:
                        switch(command.getCostType()) {
                            case EXPERIENCE:
                                if(!command.hasEXP(p)) {
                                    e.setCancelled(true);
                                    p.sendMessage(MessageUtils.NO_EXPERIENCE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_cost%", String.valueOf(command.getCost(p)), "%command_name%", eCommand));
                                    return;
                                }
                                if(p.hasPermission("commandsmanager.*") || p.hasPermission(command.getPermission())) {
                                    command.withdrawEXP(p);
                                    instance.getActionManager().execute(p, command.getActionsOnSuccess(), eCommand, command);
                                } else {
                                    e.setCancelled(true);
                                    if(!delay.contains(p))
                                        Delay.delay(p, eCommand, command);
                                    instance.getActionManager().execute(p, command.getActionsOnWait(), eCommand, command);
                                }
                                return;
                            case MONEY:
                                if(!command.hasMoney(p)) {
                                    e.setCancelled(true);
                                    p.sendMessage(MessageUtils.NO_MONEY.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_cost%", String.valueOf(command.getCost(p)), "%command_name%", eCommand));
                                    return;
                                }
                                if(p.hasPermission("commandsmanager.*") || p.hasPermission(command.getPermission())) {
                                    instance.getEconomy().withdrawPlayer(p, command.getCost(p));
                                    instance.getActionManager().execute(p, command.getActionsOnSuccess(), eCommand, command);
                                } else {
                                    e.setCancelled(true);
                                    if(!delay.contains(p))
                                        Delay.delay(p, eCommand, command);
                                    instance.getActionManager().execute(p, command.getActionsOnWait(), eCommand, command);
                                }
                                return;
                            case CUSTOM:
                                if(!command.hasCustom(p)) {
                                    e.setCancelled(true);
                                    p.sendMessage(MessageUtils.NO_CUSTOM.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_customMaterial%", command.getCustomMaterial().name(), "%command_cost%", String.valueOf(command.getCost(p)), "%command_name%", eCommand));
                                    return;
                                }
                                if(p.hasPermission("commandsmanager.*") || p.hasPermission(command.getPermission())) {
                                    command.withdrawCustom(p);
                                    instance.getActionManager().execute(p, command.getActionsOnSuccess(), eCommand, command);
                                } else {
                                    e.setCancelled(true);
                                    if(!delay.contains(p))
                                        Delay.delay(p, eCommand, command);
                                    instance.getActionManager().execute(p, command.getActionsOnWait(), eCommand, command);
                                }
                                return;
                        }
                        return;
                }
            }
        }
    }

}
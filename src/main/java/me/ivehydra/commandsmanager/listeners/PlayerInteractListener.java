package me.ivehydra.commandsmanager.listeners;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.CommandManager;
import me.ivehydra.commandsmanager.command.CommandSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final CommandsManager instance = CommandsManager.getInstance();

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        CommandManager commandManager = instance.getCommandManager();
        CommandSettings commandSettings = commandManager.getCommandSettings();

        if(commandSettings.isInteract() && instance.getDelay().contains(p)) {
            instance.getDelayFailed().add(p);
            instance.getDelay().remove(p);
        }
    }

}

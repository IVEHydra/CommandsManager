package me.ivehydra.commandsmanager.listeners;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.CommandManager;
import me.ivehydra.commandsmanager.command.CommandSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

    private final CommandsManager instance = CommandsManager.getInstance();

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        CommandManager commandManager = instance.getCommandManager();
        CommandSettings commandSettings = commandManager.getCommandSettings();

        if(commandSettings.isChat() && instance.getDelay().contains(p)) {
            instance.getDelayFailed().add(p);
            instance.getDelay().remove(p);
        }
    }

}

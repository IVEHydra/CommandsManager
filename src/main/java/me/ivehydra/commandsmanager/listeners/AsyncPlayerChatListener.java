package me.ivehydra.commandsmanager.listeners;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.CommandManager;
import me.ivehydra.commandsmanager.command.CommandSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class AsyncPlayerChatListener implements Listener {

    private final CommandsManager instance = CommandsManager.getInstance();

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        CommandManager commandManager = instance.getCommandManager();
        CommandSettings commandSettings = commandManager.getCommandSettings();
        Set<Player> delay = instance.getDelay();
        Set<Player> delayFailed = instance.getDelayFailed();

        if(commandSettings.isChat() && delay.contains(p)) {
            delayFailed.add(p);
            delay.remove(p);
        }
    }

}

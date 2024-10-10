package me.ivehydra.commandsmanager.listeners;

import me.ivehydra.commandsmanager.CommandsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final CommandsManager instance = CommandsManager.getInstance();

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        instance.getDelay().remove(p);
        instance.getDelayFailed().remove(p);
    }

}

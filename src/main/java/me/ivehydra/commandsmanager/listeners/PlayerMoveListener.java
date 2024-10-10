package me.ivehydra.commandsmanager.listeners;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.CommandManager;
import me.ivehydra.commandsmanager.command.CommandSettings;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

public class PlayerMoveListener implements Listener {

    private final CommandsManager instance = CommandsManager.getInstance();

    @EventHandler
    public void onPlayerMoveListener(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        CommandManager commandManager = instance.getCommandManager();
        CommandSettings commandSettings = commandManager.getCommandSettings();
        Location to = e.getTo();
        Location from = e.getFrom();

        if(commandSettings.isMove() && instance.getDelay().contains(p) && (Objects.requireNonNull(to).getBlockX() != from.getBlockX() || to.getBlockY() != from.getBlockY() || to.getBlockZ() != from.getBlockZ())) {
            instance.getDelayFailed().add(p);
            instance.getDelay().remove(p);
        }
    }

}

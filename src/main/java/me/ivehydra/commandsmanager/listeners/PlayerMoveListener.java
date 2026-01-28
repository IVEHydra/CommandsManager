package me.ivehydra.commandsmanager.listeners;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.CommandManager;
import me.ivehydra.commandsmanager.command.CommandSettings;
import me.ivehydra.commandsmanager.utils.BossBarUtils;
import me.ivehydra.commandsmanager.utils.VersionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.Objects;

public class PlayerMoveListener implements Listener {

    private final CommandsManager instance = CommandsManager.getInstance();

    @EventHandler
    public void onPlayerMoveListener(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        CommandManager commandManager = instance.getCommandManager();
        CommandSettings commandSettings = commandManager.getCommandSettings();
        List<String> delay = instance.getDelay();
        List<String> delayFailed = instance.getDelayFailed();
        String name = p.getName();
        Location to = e.getTo();
        Location from = e.getFrom();

        if(commandSettings.isMove() && delay.contains(name) && (Objects.requireNonNull(to).getBlockX() != from.getBlockX() || to.getBlockY() != from.getBlockY() || to.getBlockZ() != from.getBlockZ())) {
            delayFailed.add(name);
            delay.remove(name);
        }

        if(!VersionUtils.isAtLeastVersion19() && BossBarUtils.contains(p) && (Objects.requireNonNull(to).getBlockX() != from.getBlockX() || to.getBlockY() != from.getBlockY() || to.getBlockZ() != from.getBlockZ()))
            BossBarUtils.teleport(p);
    }

}

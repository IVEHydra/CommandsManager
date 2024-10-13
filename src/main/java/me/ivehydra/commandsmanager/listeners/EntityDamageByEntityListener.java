package me.ivehydra.commandsmanager.listeners;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.CommandManager;
import me.ivehydra.commandsmanager.command.CommandSettings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Set;

public class EntityDamageByEntityListener implements Listener {

    private final CommandsManager instance = CommandsManager.getInstance();

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();

        if(entity instanceof Player || damager instanceof Player) {
            Player p = (Player) (entity instanceof Player ? entity : damager);
            CommandManager commandManager = instance.getCommandManager();
            CommandSettings commandSettings = commandManager.getCommandSettings();
            Set<Player> delay = instance.getDelay();
            Set<Player> delayFailed = instance.getDelayFailed();

            if(commandSettings.isDamage() && delay.contains(p)) {
                delayFailed.add(p);
                delay.remove(p);
            }
        }

    }

}

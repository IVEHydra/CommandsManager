package me.ivehydra.commandsmanager.listeners;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.cooldown.CooldownManager;
import me.ivehydra.commandsmanager.cooldown.PlayerCooldown;
import me.ivehydra.commandsmanager.mysql.MySQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class PlayerJoinListener implements Listener {

    private final CommandsManager instance = CommandsManager.getInstance();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        CooldownManager cooldownManager = instance.getCooldownManager();
        String uuid = p.getUniqueId().toString();
        String name = p.getName();

        if(MySQL.isEnabled()) {
            MySQL.update(uuid, name);
            MySQL.getPlayerByUUID(uuid, pc -> {
                cooldownManager.removePlayerCooldown(name);
                if(pc != null)
                    cooldownManager.addPlayerCooldown(pc);
                else {
                    MySQL.setCooldown(uuid, name, null);
                    cooldownManager.addPlayerCooldown(new PlayerCooldown(uuid, name, new ArrayList<>()));
                }
            });
        } else {
            PlayerCooldown pc = cooldownManager.getPlayerFromUUID(uuid);
            if(pc == null) {
                pc = new PlayerCooldown(uuid, name, new ArrayList<>());
                cooldownManager.addPlayerCooldown(pc);
            } else pc.setName(name);
        }

    }

}

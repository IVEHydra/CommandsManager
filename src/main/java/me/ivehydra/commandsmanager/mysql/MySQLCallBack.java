package me.ivehydra.commandsmanager.mysql;

import me.ivehydra.commandsmanager.cooldown.PlayerCooldown;

public interface MySQLCallBack {

    void close(PlayerCooldown pc);

}

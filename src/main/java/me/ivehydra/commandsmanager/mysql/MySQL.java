package me.ivehydra.commandsmanager.mysql;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.cooldown.Cooldown;
import me.ivehydra.commandsmanager.cooldown.PlayerCooldown;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private static final CommandsManager instance = CommandsManager.getInstance();

    public static boolean isEnabled() { return instance.getConfig().getBoolean("mySQL.enabled"); }

    public static void createTable() {
        try(Connection connection = instance.getMySQLConnection().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS cooldowns (`UUID` varchar(200), `PLAYER_NAME` varchar(50), `COMMAND` varchar(50), `COOLDOWN` LONG )");
            statement.executeUpdate();
        } catch(SQLException e) {
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Failed to create the 'cooldowns' table.");
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
        }
    }

    public static void getPlayerByUUID(String uuid, MySQLCallBack callback) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            String name = null;
            List<Cooldown> cooldowns = new ArrayList<>();
            try(Connection connection = instance.getMySQLConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM cooldowns WHERE uuid=?");
                statement.setString(1, uuid);
                try(ResultSet result = statement.executeQuery()) {
                    while(result.next()) {
                        name = result.getString("PLAYER_NAME");
                        String command = result.getString("COMMAND");
                        long cooldown = result.getLong("COOLDOWN");
                        cooldowns.add(new Cooldown(command, cooldown));
                    }
                }
            } catch(SQLException e) {
                instance.sendLog("[CommandsManager]" + ChatColor.RED + " An error occurred while retrieving player data for UUID: " + uuid);
                instance.sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
            }
            PlayerCooldown cooldown = !cooldowns.isEmpty() ? new PlayerCooldown(uuid, name, cooldowns) : null;
            Bukkit.getScheduler().runTask(instance, () -> callback.close(cooldown));

        });
    }

    public static void setCooldown(String uuid, String name, Cooldown cooldown) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            try(Connection connection = instance.getMySQLConnection().getConnection()) {
                if(cooldown == null) {
                    try(PreparedStatement statement = connection.prepareStatement("INSERT INTO cooldowns (UUID,PLAYER_NAME,COMMAND,COOLDOWN) VALUE (?,?,?,?)")) {
                        statement.setString(1, uuid);
                        statement.setString(2, name);
                        statement.setString(3, "name");
                        statement.setLong(4, 0);
                        statement.executeUpdate();
                    }
                    return;
                }
                int rows;
                try(PreparedStatement statement = connection.prepareStatement("UPDATE cooldowns SET cooldown=?, player_name=? WHERE (uuid=? AND command=?)")) {
                    statement.setLong(1, cooldown.getCooldown());
                    statement.setString(2, name);
                    statement.setString(3, uuid);
                    statement.setString(4, cooldown.getCommand());
                    rows = statement.executeUpdate();
                }
                if(rows == 0) {
                    try(PreparedStatement statement = connection.prepareStatement("INSERT INTO cooldowns (UUID,PLAYER_NAME,COMMAND,COOLDOWN) VALUE (?,?,?,?)")) {
                        statement.setString(1, uuid);
                        statement.setString(2, name);
                        statement.setString(3, cooldown.getCommand());
                        statement.setLong(4, cooldown.getCooldown());
                        statement.executeUpdate();
                    }
                }
            } catch(SQLException e) {
                instance.sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
            }
        });
    }

    public static void update(String uuid, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            try(Connection connection = instance.getMySQLConnection().getConnection()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE cooldowns SET player_name=? WHERE (uuid=?)");
                statement.setString(1, name);
                statement.setString(2, uuid);
                statement.executeUpdate();
            } catch(SQLException e) {
                instance.sendLog("[CommandsManager]" + ChatColor.RED + " An error occurred while updating player data for UUID: " + uuid);
                instance.sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
            }
        });
    }

}

package me.ivehydra.commandsmanager.mysql;

import me.ivehydra.commandsmanager.CommandsManager;
import net.md_5.bungee.api.ChatColor;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnection {

    private final CommandsManager instance = CommandsManager.getInstance();
    private ConnectionHikari connection;

    public MySQLConnection(String host, int port, String username, String password, String database) {
        try {
            connection = new ConnectionHikari(host, port, username, password, database);
            connection.getHikari().getConnection();
        } catch(SQLException e) {
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Failed to create a MySQL Connection. Please check the connection configuration and ensure that MySQL server is reachable.");
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            return connection.getHikari().getConnection();
        } catch(SQLException e) {
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " An error occurred while attempting to retrieve a database connection. Please check the database configuration.");
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
            return null;
        }
    }

    public boolean isConnected() { return connection != null; }

    public void disconnect() { if(isConnected()) connection.close(); }

}

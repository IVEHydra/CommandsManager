package me.ivehydra.commandsmanager.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionHikari {

    private final HikariDataSource hikari;

    public ConnectionHikari(String host, int port, String username, String password, String database) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("leakDetectionThreshold", "true");
        config.addDataSourceProperty("verifyServerCertificate", "false");
        config.addDataSourceProperty("useSSL", "false");
        config.setConnectionTimeout(5000);
        hikari = new HikariDataSource(config);
    }

    public HikariDataSource getHikari() { return hikari; }

    public void close() { if(hikari != null) hikari.close(); }

}

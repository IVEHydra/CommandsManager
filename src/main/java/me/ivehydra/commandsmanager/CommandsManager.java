package me.ivehydra.commandsmanager;

import me.ivehydra.commandsmanager.action.ActionManager;
import me.ivehydra.commandsmanager.api.PlaceholderAPI;
import me.ivehydra.commandsmanager.command.CommandManager;
import me.ivehydra.commandsmanager.commands.CommandsManagerCommands;
import me.ivehydra.commandsmanager.commands.CommandsManagerTabCompleter;
import me.ivehydra.commandsmanager.cooldown.CooldownManager;
import me.ivehydra.commandsmanager.listeners.*;
import me.ivehydra.commandsmanager.mysql.MySQL;
import me.ivehydra.commandsmanager.mysql.MySQLConnection;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import me.ivehydra.commandsmanager.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class CommandsManager extends JavaPlugin {

    private static CommandsManager instance;
    private Economy economy;
    private File cooldownsFile;
    private YamlConfiguration cooldownsConfiguration;
    private MySQLConnection mySQLConnection;
    private CommandManager commandManager;
    private ActionManager actionManager;
    private CooldownManager cooldownManager;
    private Set<Player> delay;
    private Set<Player> delayFailed;

    @Override
    public void onEnable() {
        instance = this;
        cooldownsConfiguration = new YamlConfiguration();
        delay = new HashSet<>();
        delayFailed = new HashSet<>();

        if(!registerEconomy()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if(isPluginPresent("PlaceholderAPI")) {
            new PlaceholderAPI().register();
            sendLog("[CommandsManager]" + ChatColor.GREEN + " PlaceholderAPI has been found. The cooldown placeholder is now available for use. The placeholders from PlaceholderAPI can now be used for Actions.");
        } else sendLog("[CommandsManager]" + ChatColor.YELLOW + " PlaceholderAPI not found. The plugin will still function correctly, but the cooldown placeholder and the placeholders from PlaceholderAPI will not be available.");

        saveDefaultConfig();
        registerCooldownsFile();

        if(MySQL.isEnabled()) {
            mySQLConnection = new MySQLConnection(getConfig().getString("mySQL.host"), getConfig().getInt("mySQL.port"), getConfig().getString("mySQL.username"), getConfig().getString("mySQL.password"), getConfig().getString("mySQL.database"));
            if(mySQLConnection.isConnected()) {
                sendLog("[CommandsManager]" + ChatColor.GREEN + " MySQL Connected.");
                MySQL.createTable();
            }
        }

        commandManager = new CommandManager();
        actionManager = new ActionManager();
        cooldownManager = new CooldownManager();

        registerCommands();
        registerListeners();

        updateChecker(version -> {
            String currentVersion = getDescription().getVersion();
            if(currentVersion.equals(version)) sendLog(MessageUtils.LATEST_VERSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%current_version%", currentVersion, "%new_version%", version));
            else instance.getConfig().getStringList(MessageUtils.NEW_VERSION.getPath()).forEach(message -> sendLog(StringUtils.getColoredString(message).replace("%prefix%", MessageUtils.PREFIX.toString()).replace("%current_version%", currentVersion).replace("%new_version%", version)));
        });
    }

    @Override
    public void onDisable() {
        instance = null;

        cooldownManager.save();
        if(MySQL.isEnabled()) {
            mySQLConnection.disconnect();
            sendLog("[CommandsManager]" + ChatColor.RED + " MySQL Disconnected.");
        }

    }

    public static CommandsManager getInstance() { return instance; }

    public boolean isPluginPresent(String plugin) { return Bukkit.getPluginManager().getPlugin(plugin) != null; }

    private boolean registerEconomy() {
        if(!isPluginPresent("Vault")) {
            sendLog("[CommandsManager]" + ChatColor.RED + " Vault Not Found!");
            return false;
        }
        RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);
        if(provider == null) {
            sendLog("[CommandsManager]" + ChatColor.RED + " Economy Plugin Not Found!");
            return false;
        }
        economy = provider.getProvider();
        return true;
    }

    public Economy getEconomy() { return this.economy; }

    public void reloadConfigFile() {
        reloadConfig();
        commandManager = new CommandManager();
    }

    private void registerCooldownsFile() {
        cooldownsFile = new File(getDataFolder(), "cooldowns.yml");
        if(!cooldownsFile.exists()) saveResource("cooldowns.yml", false);
        try {
            cooldownsConfiguration.load(cooldownsFile);
        } catch(IOException | InvalidConfigurationException e) {
            sendLog("[CommandsManager]" + ChatColor.RED + " An error occurred while trying to load 'cooldowns.yml'.");
            sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
        }
    }

    public void saveCooldownsFile() {
        if(cooldownsFile == null) return;
        try {
            getCooldownsFile().save(cooldownsFile);
        } catch(IOException e) {
            sendLog("[CommandsManager]" + ChatColor.RED + " An error occurred while saving 'cooldowns.yml'.");
            sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
        }
    }

    public YamlConfiguration getCooldownsFile() { return this.cooldownsConfiguration; }

    public MySQLConnection getMySQLConnection() { return this.mySQLConnection; }

    public CommandManager getCommandManager() { return this.commandManager; }

    public ActionManager getActionManager() { return this.actionManager; }

    public CooldownManager getCooldownManager() { return this.cooldownManager; }

    public Set<Player> getDelay() { return delay; }

    public Set<Player> getDelayFailed() { return delayFailed; }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("commandsmanager")).setExecutor(new CommandsManagerCommands());
        Objects.requireNonNull(getCommand("commandsmanager")).setTabCompleter(new CommandsManagerTabCompleter());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerCommandPreProcessListener(), this);
        pm.registerEvents(new PlayerJoinListener(), this);
        pm.registerEvents(new AsyncPlayerChatListener(), this);
        pm.registerEvents(new PlayerMoveListener(), this);
        pm.registerEvents(new PlayerInteractListener(), this);
        pm.registerEvents(new EntityDamageByEntityListener(), this);
        pm.registerEvents(new PlayerQuitListener(), this);
    }

    private void updateChecker(Consumer<String> consumer) {
        if(!instance.getConfig().getBoolean("updateCheck")) return;
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try(InputStream stream = new URL("https://api.spigotmc.org/legacy/update.php?resource=111108").openStream()) {
                Scanner scanner = new Scanner(stream);
                if(scanner.hasNext()) consumer.accept(scanner.next());
            } catch(IOException e) {
                sendLog("[CommandsManager]" + ChatColor.RED + " Can't find a new version!");
                sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
            }
        });
    }

    public void sendLog(String string) { getServer().getConsoleSender().sendMessage(string); }

}

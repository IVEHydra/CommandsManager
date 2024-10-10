package me.ivehydra.commandsmanager.command;

import me.ivehydra.commandsmanager.CommandsManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandManager {

    private final CommandsManager instance = CommandsManager.getInstance();
    private final CommandSettings commandSettings;
    private final List<Command> commands;

    public CommandManager() {
        this.commands = new ArrayList<>();
        this.commandSettings = loadSettings();

        load();
    }

    private CommandSettings loadSettings() {
        ConfigurationSection section = instance.getConfig().getConfigurationSection("commandsSettings");
        boolean colons = true;
        boolean command = true;
        boolean chat = true;
        boolean move = true;
        boolean interact = true;
        boolean damage = true;
        if(section != null) {
            colons = section.getBoolean("blockColons");
            ConfigurationSection delay = section.getConfigurationSection("delayCommandCancel");
            if(delay != null) {
                command = delay.getBoolean("onPlayerCommandProcessEvent");
                chat = delay.getBoolean("onPlayerChatEvent");
                move = delay.getBoolean("onPlayerMoveEvent");
                interact = delay.getBoolean("onPlayerInteractEvent");
                damage = delay.getBoolean("onDamageEvent");

                return new CommandSettings(colons, command, chat, move, interact, damage);
            }
        }
        return new CommandSettings(colons, command, chat, move, interact, damage);
    }

    private void load() {
        for(String id : Objects.requireNonNull(instance.getConfig().getConfigurationSection("commandsSettings.commands")).getKeys(false)) {
            ConfigurationSection section = instance.getConfig().getConfigurationSection("commandsSettings.commands." + id);
            if(section != null) {
                CommandType type = CommandType.valueOf(section.getString("type"));
                Command command = null;
                switch(type) {
                    case BLOCK:
                        command = blockCommand(section);
                        break;
                    case COOLDOWN:
                        command = cooldownCommand(section);
                        break;
                    case DELAY:
                        command = delayCommand(section);
                        break;
                }
                if(command != null) this.commands.add(command);
            }
        }
    }

    private Command blockCommand(ConfigurationSection section) { return new Command(section.getString("permission"), section.getStringList("worlds"), section.getStringList("commands"), section.getStringList("actions")); }

    private Command cooldownCommand(ConfigurationSection section) { return new Command(section.getString("permission"), section.getStringList("time.custom"), section.getInt("time.default"), section.getStringList("worlds"), section.getStringList("commands"), section.getStringList("actions")); }

    private Command delayCommand(ConfigurationSection section) { return new Command(section.getString("permission"), section.getStringList("time.custom"), section.getInt("time.default"), section.getStringList("cost.custom"), section.getInt("cost.default"), section.getString("cost.type"), section.getString("loadingBarLength"), section.getStringList("worlds"), section.getStringList("commands"), section.getStringList("actions.onWait"), section.getStringList("actions.onSuccess"), section.getStringList("actions.onFail"));  }

    public CommandSettings getCommandSettings() { return commandSettings; }

    public List<Command> getCommands() { return commands; }

    public Command getCommandByIdentifier(String identifier) { return getCommands().stream().filter(command -> command.isCommand(identifier)).findFirst().orElse(null); }

}

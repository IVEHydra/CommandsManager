package me.ivehydra.commandsmanager.command;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.modules.BlockModule;
import me.ivehydra.commandsmanager.command.modules.CooldownModule;
import me.ivehydra.commandsmanager.command.modules.DelayModule;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if(section != null) {
            boolean colons = section.getBoolean("blockColons");
            ConfigurationSection delay = section.getConfigurationSection("delayCommandCancel");
            if(delay != null) {
                boolean command = delay.getBoolean("onPlayerCommandProcessEvent");
                boolean chat = delay.getBoolean("onPlayerChatEvent");
                boolean move = delay.getBoolean("onPlayerMoveEvent");
                boolean interact = delay.getBoolean("onPlayerInteractEvent");
                boolean damage = delay.getBoolean("onDamageEvent");

                return new CommandSettings(colons, command, chat, move, interact, damage);
            }
        }
        return new CommandSettings(true, true, true, true, true, true);
    }

    private void load() {
        ConfigurationSection section = instance.getConfig().getConfigurationSection("commandsSettings.commands");
        if(section != null) {
            for(String id : section.getKeys(false)) {
                ConfigurationSection commandSection = section.getConfigurationSection(id);
                if(commandSection != null) {
                    Command command = null;
                    String typeString = commandSection.getString("type");
                    try {
                        CommandType type = CommandType.valueOf(typeString);
                        switch(type) {
                            case BLOCK:
                                command = blockCommand(commandSection);
                                break;
                            case COOLDOWN_DELAY:
                                command = cooldownDelayCommand(commandSection);
                                break;
                            case COOLDOWN:
                                command = cooldownCommand(commandSection);
                                break;
                            case DELAY:
                                command = delayCommand(commandSection);
                                break;
                        }
                    } catch(IllegalArgumentException e) {
                        instance.sendLog("[CommandsManager]" + ChatColor.RED + " Invalid CommandType: " + typeString);
                    }

                    if(command != null)
                        commands.add(command);
                }
            }
        }
    }

    private Command blockCommand(ConfigurationSection section) { return new Command(section.getString("permission"), section.getStringList("worlds"), section.getStringList("commands"), Optional.of(new BlockModule(section.getStringList("actions"))), Optional.empty(), Optional.empty()); }

    private Command cooldownDelayCommand(ConfigurationSection section) {
        Material material = parseCostType(section);

        return new Command( section.getString("permission"), section.getStringList("worlds"), section.getStringList("commands"),Optional.empty(), Optional.of(new CooldownModule(section.getStringList("time.cooldown.custom"), section.getInt("time.cooldown.default"), section.getStringList("actions.onFail.cooldown"))), Optional.of(new DelayModule( section.getStringList("time.delay.custom"), section.getInt("time.delay.default"), section.getString("loadingBarLength"), section.getString("cost.type"), material, section.getStringList("cost.custom"), section.getInt("cost.default"), section.getStringList("actions.onWait"), section.getStringList("actions.onSuccess"), section.getStringList("actions.onFail.delay"))));
    }

    private Command cooldownCommand(ConfigurationSection section) { return new Command(section.getString("permission"), section.getStringList("worlds"), section.getStringList("commands"), Optional.empty(), Optional.of(new CooldownModule(section.getStringList("time.custom"), section.getInt("time.default"), section.getStringList("actions"))), Optional.empty()); }

    private Command delayCommand(ConfigurationSection section) {
        Material material = parseCostType(section);

        return new Command(section.getString("permission"), section.getStringList("worlds"), section.getStringList("commands"), Optional.empty(), Optional.empty(), Optional.of(new DelayModule(section.getStringList("time.custom"), section.getInt("time.default"), section.getString("loadingBarLength"), section.getString("cost.type"), material, section.getStringList("cost.custom"), section.getInt("cost.default"), section.getStringList("actions.onWait"), section.getStringList("actions.onSuccess"), section.getStringList("actions.onFail"))));
    }

    private Material parseCostType(ConfigurationSection section) {
        String costType = section.getString("cost.type");

        if(costType != null && costType.startsWith("CUSTOM:")) {
            String name = costType.split(":")[1].toUpperCase();
            Material material = Material.getMaterial(name);

            if(material == null) {
                instance.sendLog("[CommandsManager]" + ChatColor.RED + " Invalid Material: " + name);
                return null;
            }
            return material;
        }
        return null;
    }

    public CommandSettings getCommandSettings() { return commandSettings; }

    public List<Command> getCommands() { return commands; }

    public Command getCommandByIdentifier(String identifier) { return getCommands().stream().filter(command -> command.isCommand(identifier)).findFirst().orElse(null); }

}

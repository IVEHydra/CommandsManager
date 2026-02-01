package me.ivehydra.commandsmanager.action;

import me.clip.placeholderapi.PlaceholderAPI;
import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.action.actions.*;
import me.ivehydra.commandsmanager.command.Command;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.*;

public class ActionManager {

    private final CommandsManager instance = CommandsManager.getInstance();
    private final Map<String, Action> actions;

    public ActionManager() {
        actions = new HashMap<>();

        load();
    }

    private void load() {
        register(
                new SoundAction(),
                new MessageAction(),
                new JSONMessageAction(),
                new TitleAction(),
                new ActionBarAction(),
                new CommandConsoleAction(),
                new CommandPlayerAction()
        );
    }

    private void register(Action... actions) { Arrays.asList(actions).forEach(action -> this.actions.put(action.getName(), action)); }

    public void execute(Player p, List<String> actions, String data, Command command) { executeActions(p, actions, 0, data, command); }

    private void executeActions(Player p, List<String> actions, int index, String data, Command command) {
        if(index >= actions.size())
            return;

        String string = actions.get(index);
        String name = StringUtils.substringBetween(string, "[", "]");
        Action action = getActionByName(name);

        if(action == null) {
            executeActions(p, actions, index + 1, data, command);
            return;
        }

        String args = string.contains(" ") ? string.split(" ", 2)[1] : "";
        args = me.ivehydra.commandsmanager.utils.StringUtils.getColoredString(args
                .replace("%prefix%", MessageUtils.PREFIX.toString())
                .replace("%player_name%", p.getName())
                .replace("%command_name%", data)
                .replace("%command_time_delay%", command.hasDelay() ? String.valueOf(command.getDelayModule().getDelayTime(p)) : "0")
                .replace("%command_time_cooldown%", command.hasCooldown() ? String.valueOf(command.getCooldownModule().getCooldownTime(p)) : "0")
                .replace("%command_cooldown%", instance.getCooldownManager().getFormattedCooldown(p, data, command))
                .replace("%command_cost%", command.hasDelay() ? String.valueOf(command.getDelayModule().getCost(p)) : "0")
        );

        if(instance.isPluginPresent("PlaceholderAPI"))
            args = PlaceholderAPI.setPlaceholders(p, args);

        action.execute(p, args, () -> executeActions(p, actions, index + 1, data, command));

    }

    private Action getActionByName(String name) { return actions.get(name.toUpperCase()); }

}

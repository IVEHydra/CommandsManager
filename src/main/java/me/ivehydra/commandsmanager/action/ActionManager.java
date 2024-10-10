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

    public void execute(Player p, List<String> actions, String data, Command command) {
        actions.forEach(string -> {
            String name = StringUtils.substringBetween(string, "[", "]");
            Action action = name == null ? null : this.actions.get(name.toUpperCase());

            if(action != null) {
                string = string.split(" ", 2)[1];
                string = me.ivehydra.commandsmanager.utils.StringUtils.getColoredString(string
                        .replace("%prefix%", MessageUtils.PREFIX.toString())
                        .replace("%player_name%", p.getName())
                        .replace("%command_name%", data)
                        .replace("%command_time%", String.valueOf(command.getTime(p)))
                        .replace("%command_cooldown%", instance.getCooldownManager().getFormattedCooldown(p, data, command))
                        .replace("%command_cost%", String.valueOf(command.getCost(p))
                ));
                string = instance.isPlaceholderAPIPresent() ? PlaceholderAPI.setPlaceholders(p, string) : string;
                action.execute(p, string);
            }
        });
    }

}

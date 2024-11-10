package me.ivehydra.commandsmanager.command.modules;

import org.bukkit.entity.Player;

import java.util.List;

public class CooldownModule {

    private final List<String> time;
    private final int defaultTime;
    private final List<String> actions;

    public CooldownModule(List<String> time, int defaultTime, List<String> actions) {
        this.time = time;
        this.defaultTime = defaultTime;
        this.actions = actions;
    }

    public int getCooldownTime(Player p) {
        if(time == null || time.isEmpty()) return defaultTime;
        return time.stream().map(key -> key.split(";")).filter(args -> args.length == 2 && p.hasPermission(args[0])).map(args -> Integer.parseInt(args[1])).findFirst().orElse(defaultTime);
    }

    public List<String> getActions() { return actions; }

}

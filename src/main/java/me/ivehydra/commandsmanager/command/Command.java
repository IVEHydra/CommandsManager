package me.ivehydra.commandsmanager.command;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.cost.CostType;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Command {

    private final CommandsManager instance = CommandsManager.getInstance();
    private final String permission;
    private final CommandType type;
    private List<String> timeList;
    private int defaultTime;
    private CostType costType;
    private List<String> costList;
    private int defaultCost;
    private String loadingBarLength;
    private final List<World> worlds;
    private final List<String> commands;
    private List<String> actions;
    private List<String> actionsOnWait;
    private List<String> actionsOnSuccess;
    private List<String> actionsOnFail;

    public Command(String permission, List<String> worlds, List<String> commands, List<String> actions) {
        this.permission = permission;
        this.type = CommandType.BLOCK;
        this.worlds = loadWorlds(worlds);
        this.commands = commands;
        this.actions = actions;
    }

    public Command(String permission, List<String> timeList, int defaultTime, List<String> worlds, List<String> commands, List<String> actions) {
        this.permission = permission;
        this.type = CommandType.COOLDOWN;
        this.timeList = timeList;
        this.defaultTime = defaultTime;
        this.worlds = loadWorlds(worlds);
        this.commands = commands;
        this.actions = actions;
    }

    public Command(String permission, List<String> timeList, int defaultTime, List<String> costList, int defaultCost, String costType, String loadingBarLength, List<String> worlds, List<String> commands, List<String> actionsOnWait, List<String> actionsOnSuccess, List<String> actionsOnFail) {
        this.permission = permission;
        this.type = CommandType.DELAY;
        this.timeList = timeList;
        this.defaultTime = defaultTime;
        this.costList = costList;
        this.defaultCost = defaultCost;
        this.costType = CostType.fromString(costType);
        this.loadingBarLength = loadingBarLength;
        this.worlds = loadWorlds(worlds);
        this.commands = commands;
        this.actionsOnWait = actionsOnWait;
        this.actionsOnSuccess = actionsOnSuccess;
        this.actionsOnFail = actionsOnFail;
    }

    public String getPermission() { return permission; }

    public CommandType getType() { return type; }

    public int getTime(Player p) {
        if(timeList == null || timeList.isEmpty()) return defaultTime;
        return timeList.stream().map(key -> key.split(";")).filter(args -> args.length == 2 && p.hasPermission(args[0])).map(args -> Integer.parseInt(args[1])).findFirst().orElse(defaultTime);
    }

    public int getLoadingBarLength(Player p) { return "%command_time%".equals(loadingBarLength) ? getTime(p) : Integer.parseInt(loadingBarLength); }

    public int getCost(Player p) {
        if(costList == null || costList.isEmpty()) return defaultCost;
        return costList.stream().map(key -> key.split(";")).filter(args -> args.length == 2 && p.hasPermission(args[0])).map(args -> Integer.parseInt(args[1])).findFirst().orElse(defaultCost);
    }

    public boolean hasMoney(Player p) { return getCost(p) > 0 && instance.getEconomy().has(p, getCost(p)); }

    public CostType getCostType() { return costType; }

    public boolean hasEXP(Player p) {
        int exp = getCost(p);
        return p.getTotalExperience() >= exp;
    }

    public void withdrawEXP(Player p) {
        int exp = getCost(p);
        int totalEXP = p.getTotalExperience();
        p.setTotalExperience(totalEXP - exp);
    }

    public boolean hasCustom(Player p) {
        String custom = getType().toString();
        if(custom.startsWith("CUSTOM:")) {
            String name = custom.split(":")[1];
            Material material = Material.getMaterial(name);
            if(material != null) {
                int required = getCost(p);
                ItemStack itemStack = new ItemStack(material, required);
                return p.getInventory().containsAtLeast(itemStack, required);
            }
        }
        return false;
    }

    public void withdrawCustom(Player p) {
        String custom = getType().toString();
        if(custom.startsWith("CUSTOM:")) {
            String name = custom.split(":")[1];
            Material material = Material.getMaterial(name);
            if(material != null) {
                int required = getCost(p);
                ItemStack itemStack = new ItemStack(material, required);
                p.getInventory().removeItem(itemStack);
            }
        }
    }

    private List<World> loadWorlds(List<String> worlds) { return worlds.stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList()); }

    public List<World> getWorlds() { return worlds; }

    public Optional<String> getCommand(String command) {
        String[] commandArgs = command.toLowerCase().split(" ");
        return commands.stream().filter(blockedCommand -> matchesCommand(commandArgs, blockedCommand.toLowerCase().split(" "))).findFirst();
    }

    public boolean isCommand(String command) { return getCommand(command).isPresent(); }

    private boolean matchesCommand(String[] commandArgs, String[] blockedCommandArgs) { return IntStream.range(0, Math.min(blockedCommandArgs.length, commandArgs.length)).allMatch(i -> blockedCommandArgs[i].equals(commandArgs[i])) && blockedCommandArgs.length <= commandArgs.length; }

    public List<String> getActions() { return actions; }

    public List<String> getActionsOnWait() { return actionsOnWait; }

    public List<String> getActionsOnSuccess() { return actionsOnSuccess; }

    public List<String> getActionsOnFail() { return actionsOnFail; }

}

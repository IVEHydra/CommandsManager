package me.ivehydra.commandsmanager.command.modules;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.cost.CostType;
import me.ivehydra.commandsmanager.utils.EXPUtils;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DelayModule {

    private final CommandsManager instance = CommandsManager.getInstance();
    private final List<String> time;
    private final int defaultTime;
    private final String loadingBarLength;
    private final CostType costType;
    private final Material material;
    private final List<String> cost;
    private final int defaultCost;
    private final List<String> actionsOnWait;
    private final List<String> actionsOnSuccess;
    private final List<String> actionsOnFail;

    public DelayModule(List<String> time, int defaultTime, String loadingBarLength, String costType, Material material, List<String> cost, int defaultCost, List<String> actionsOnWait, List<String> actionsOnSuccess, List<String> actionsOnFail) {
        this.time = time;
        this.defaultTime = defaultTime;
        this.loadingBarLength = loadingBarLength;
        this.costType = CostType.fromString(costType);
        this.material = material;
        this.cost = cost;
        this.defaultCost = defaultCost;
        this.actionsOnWait = actionsOnWait;
        this.actionsOnSuccess = actionsOnSuccess;
        this.actionsOnFail = actionsOnFail;
    }

    public int getDelayTime(Player p) {
        if(time == null || time.isEmpty()) return defaultTime;
        return time.stream().map(key -> key.split(";")).filter(args -> args.length == 2 && p.hasPermission(args[0])).map(args -> Integer.parseInt(args[1])).findFirst().orElse(defaultTime);
    }

    public int getLoadingBarLength(Player p) { return "%command_time%".equals(loadingBarLength) ? getDelayTime(p) : Integer.parseInt(loadingBarLength); }

    public int getCost(Player p) {
        if(cost == null || cost.isEmpty()) return defaultCost;
        return cost.stream().map(key -> key.split(";")).filter(args -> args.length == 2 && p.hasPermission(args[0])).map(args -> Integer.parseInt(args[1])).findFirst().orElse(defaultCost);
    }

    public boolean hasMoney(Player p, String eCommand) {
        int cost = getCost(p);
        switch(costType) {
            case EXPERIENCE:
                if(EXPUtils.getPlayerEXP(p) >= cost) return true;
                else {
                    p.sendMessage(MessageUtils.NO_EXPERIENCE.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_cost%", String.valueOf(getCost(p)), "%command_name%", eCommand));
                    return false;
                }
            case MONEY:
                if(instance.getEconomy().has(p, cost))
                    return true;
                else {
                    p.sendMessage(MessageUtils.NO_MONEY.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_cost%", String.valueOf(getCost(p)), "%command_name%", eCommand));
                    return false;
                }
            case CUSTOM:
                if(p.getInventory().containsAtLeast(new ItemStack(material), cost))
                    return true;
                else {
                    p.sendMessage(MessageUtils.NO_CUSTOM.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_customMaterial%", material.name(), "%command_cost%", String.valueOf(getCost(p)), "%command_name%", eCommand));
                    return false;
                }
            default:
                return false;
        }
    }

    public void withdrawMoney(Player p) {
        int cost = getCost(p);
        switch(costType) {
            case EXPERIENCE:
                EXPUtils.changePlayerEXP(p, -cost);
                break;
            case MONEY:
                instance.getEconomy().withdrawPlayer(p, cost);
                break;
            case CUSTOM:
                p.getInventory().removeItem(new ItemStack(material, cost));
                break;
        }
    }

    public List<String> getActionsOnWait() { return actionsOnWait; }

    public List<String> getActionsOnSuccess() { return actionsOnSuccess; }

    public List<String> getActionsOnFail() { return actionsOnFail; }

}

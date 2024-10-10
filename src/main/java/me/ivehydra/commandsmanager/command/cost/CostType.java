package me.ivehydra.commandsmanager.command.cost;

import me.ivehydra.commandsmanager.CommandsManager;
import net.md_5.bungee.api.ChatColor;

public enum CostType {

    EXPERIENCE,
    MONEY,
    CUSTOM;

    private static final CommandsManager instance = CommandsManager.getInstance();

    public static CostType fromString(String type) {
        try {
            return CostType.valueOf(type.toUpperCase());
        } catch(IllegalArgumentException e) {
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Unknown Cost Type: " + type);
            return null;
        }
    }

}

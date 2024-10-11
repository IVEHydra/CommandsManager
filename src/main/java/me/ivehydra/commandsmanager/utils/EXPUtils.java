package me.ivehydra.commandsmanager.utils;

import org.bukkit.entity.Player;

public class EXPUtils {

    private static int getEXPToLevelUp(int level) {
        if(level <= 15)
            return 2 * level + 7;
        else if(level <= 30)
            return 5 * level - 38;
        else
            return 9 * level - 158;
    }

    private static int getEXPAtLevel(int level) {
        if(level <= 16)
            return (int) (Math.pow(level, 2) + 6 * level);
        else if(level <= 31)
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360.0);
        else
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220.0);
    }

    public static int getPlayerEXP(Player p) {
        int exp = 0;
        int level = p.getLevel();

        exp += getEXPAtLevel(level);
        exp += Math.round(getEXPToLevelUp(level) * p.getExp());

        return exp;
    }

    public static void changePlayerEXP(Player p, int exp) {
        int current = getPlayerEXP(p);

        p.setExp(0);
        p.setLevel(0);

        int newExp = current + exp;
        p.giveExp(newExp);

    }

}

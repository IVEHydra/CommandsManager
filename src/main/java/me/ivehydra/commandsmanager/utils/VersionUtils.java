package me.ivehydra.commandsmanager.utils;

import me.ivehydra.commandsmanager.CommandsManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class VersionUtils {

    private static final CommandsManager instance = CommandsManager.getInstance();

    private static boolean isVersionAtLeast(int major, int min) {
        String version = Bukkit.getVersion();
        String numericVersion = version.split("-")[0];
        String[] args = numericVersion.split("\\.");
        try {
            int serverMajor = Integer.parseInt(args[0]);
            int serverMin = Integer.parseInt(args[1]);
            if(serverMajor > major)
                return true;
            if(serverMajor == major)
                return serverMin >= min;
            return false;
        } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Error while parsing Bukkit Version: " + version);
            instance.sendLog("[Commandsmanager]" + ChatColor.RED + " Error details: " + e.getMessage());
            return false;
        }
    }

    public static boolean isAtLeastVersion116() { return isVersionAtLeast(1, 16); }

}

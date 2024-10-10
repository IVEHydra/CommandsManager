package me.ivehydra.commandsmanager.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String getColoredString(String string) {
        if(Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20") || Bukkit.getVersion().contains("1.21")) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(string);

            while(matcher.find()) {
                String color = string.substring(matcher.start(), matcher.end());
                string = string.replace(color, String.valueOf(ChatColor.of(color)));
                matcher = pattern.matcher(string);
            }
        }

        string = ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }

}

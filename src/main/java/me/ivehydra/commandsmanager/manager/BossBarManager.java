package me.ivehydra.commandsmanager.manager;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.utils.BossBarUtils;
import me.ivehydra.commandsmanager.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {

    private static final CommandsManager instance = CommandsManager.getInstance();
    private static BossBar bossBar;

    private static boolean isEnabled() { return instance.getConfig().getBoolean("loadingBar.bossBar.enabled"); }

    public static void show(Player p, String text, float percentage) {
        if(!isEnabled()) return;

        if(VersionUtils.isAtLeastVersion19())
            showModern(p, text, percentage);
        else
            showLegacy(p, text, percentage);
    }

    private static void showModern(Player p, String text, float percentage) {

        if(bossBar == null) {
            BarColor color = BarColor.valueOf(instance.getConfig().getString("loadingBar.bossBar.color"));
            BarStyle style = BarStyle.valueOf(instance.getConfig().getString("loadingBar.bossBar.style"));

            bossBar = Bukkit.createBossBar(text, color, style);
            bossBar.addPlayer(p);
        }

        bossBar.setTitle(text);
        bossBar.setProgress(percentage);
    }

    private static void showLegacy(Player p, String text, float percentage) {
        float finalPercentage = Math.max(1F, percentage * 300);

        BossBarUtils.addWither(p, text, finalPercentage);
        BossBarUtils.updateText(p, text);
        BossBarUtils.updateHealth(p, finalPercentage);

    }

    public static void remove(Player p) {
        if(!isEnabled()) return;

        if(VersionUtils.isAtLeastVersion19()) {
            if(bossBar != null) {
                bossBar.removePlayer(p);
                if(bossBar.getPlayers().isEmpty())
                    bossBar = null;
            }
        } else
            BossBarUtils.removeWither(p);
    }

}

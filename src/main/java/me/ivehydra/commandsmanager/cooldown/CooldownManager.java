package me.ivehydra.commandsmanager.cooldown;

import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.Command;
import me.ivehydra.commandsmanager.mysql.MySQL;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CooldownManager {

    private final CommandsManager instance = CommandsManager.getInstance();
    private final List<PlayerCooldown> players;

    public CooldownManager() {
        this.players = new ArrayList<>();

        load();
    }

    private void load() {
        if(MySQL.isEnabled() || !instance.getCooldownsFile().contains("cooldowns")) return;
        ConfigurationSection section = instance.getCooldownsFile().getConfigurationSection("cooldowns");
        if(section == null) return;

        section.getKeys(false).forEach(uuid -> {
            String name = section.getString(uuid + ".name");
            List<Cooldown> cooldowns = new ArrayList<>();
            ConfigurationSection commandSection = section.getConfigurationSection(uuid);
            if(commandSection != null) {
                commandSection.getKeys(false).forEach(command -> {
                    if(!command.equals("name")) {
                        long cooldown = section.getLong(uuid + "." + command + ".cooldown");
                        cooldowns.add(new Cooldown(command, cooldown));
                    }
                });
            }
            players.add(new PlayerCooldown(uuid, name, cooldowns));
        });
    }

    public void save() {
        if(MySQL.isEnabled()) return;

        players.forEach(pc -> {
            String uuid = pc.getUUID();
            instance.getCooldownsFile().set("cooldowns." + uuid + ".name", pc.getName());
            pc.getCooldowns().forEach(cooldown -> instance.getCooldownsFile().set("cooldowns." + uuid + "." + cooldown.getCommand() + ".cooldown", cooldown.getCooldown()));
        });
        instance.saveCooldownsFile();
    }

    public void addPlayerCooldown(PlayerCooldown pc) { players.add(pc); }

    public void removePlayerCooldown(String name) { players.removeIf(pc -> pc.getName().equals(name)); }

    public PlayerCooldown getPlayerFromUUID(String uuid) { return players.stream().filter(pc -> pc.getUUID().equals(uuid)).findFirst().orElse(null); }

    public void setCooldown(Player p, String command, long time) {
        PlayerCooldown pc = getPlayerFromUUID(p.getUniqueId().toString());
        if(pc == null) {
            pc = new PlayerCooldown(p.getUniqueId().toString(), p.getName(), new ArrayList<>());
            addPlayerCooldown(pc);
        }
        Cooldown cooldown = pc.getCooldownByCommand(command);
        if(cooldown != null) cooldown.setCooldown(time);
        else pc.getCooldowns().add(new Cooldown(command, time));
        if(MySQL.isEnabled()) MySQL.setCooldown(pc.getUUID(), pc.getName(), cooldown);
    }

    public long getCooldown(Player p, String command) {
        PlayerCooldown pc = getPlayerFromUUID(p.getUniqueId().toString());
        return pc != null && pc.getCooldownByCommand(command) != null ? pc.getCooldownByCommand(command).getCooldown() : 0;
    }

    public String getFormattedCooldown(Player p, String eCommand, Command command) {
        long time = getCooldown(p, eCommand);
        long currentTime = System.currentTimeMillis();
        long cooldown = command.getTime(p) * 1000L;
        long remainingTime = (time + cooldown) - currentTime;

        if(remainingTime > 0)
            return formatTime(remainingTime);
        else
            return "0";
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long days = seconds / 86400;
        seconds %= 86400;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        if(days == 0 && hours == 0 && minutes == 0 && seconds == 0) return "0";

        StringBuilder builder = new StringBuilder();
        if(days > 0) builder.append(days).append(MessageUtils.DAYS).append(" ");
        if(hours > 0) builder.append(hours).append(MessageUtils.HOURS).append(" ");
        if(minutes > 0) builder.append(minutes).append(MessageUtils.MINUTES).append(" ");
        if(seconds > 0) builder.append(seconds).append(MessageUtils.SECONDS);

        return builder.toString().trim();
    }

}

package me.ivehydra.commandsmanager.cooldown;

import java.util.List;

public class PlayerCooldown {

    private final String uuid;
    private String name;
    private final List<Cooldown> cooldowns;

    public PlayerCooldown(String uuid, String name, List<Cooldown> cooldowns) {
        this.uuid = uuid;
        this.name = name;
        this.cooldowns = cooldowns;
    }

    public String getUUID() { return this.uuid; }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public List<Cooldown> getCooldowns() { return this.cooldowns; }

    public Cooldown getCooldownByCommand(String command) { return cooldowns.stream().filter(c -> c.getCommand().equals(command)).findFirst().orElse(null); }

}

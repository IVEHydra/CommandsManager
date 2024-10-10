package me.ivehydra.commandsmanager.cooldown;

public class Cooldown {

    private final String command;
    private long cooldown;

    public Cooldown(String command, long cooldown) {
        this.command = command;
        this.cooldown = cooldown;
    }

    public String getCommand() { return this.command; }

    public long getCooldown() { return this.cooldown; }

    public void setCooldown(long cooldown) { this.cooldown = cooldown; }

}

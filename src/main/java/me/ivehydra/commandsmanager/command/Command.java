package me.ivehydra.commandsmanager.command;

import me.ivehydra.commandsmanager.command.modules.BlockModule;
import me.ivehydra.commandsmanager.command.modules.CooldownModule;
import me.ivehydra.commandsmanager.command.modules.DelayModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Command {

    private final String permission;
    private final List<World> worlds;
    private final List<String> commands;
    private final Optional<BlockModule> blockModule;
    private final Optional<CooldownModule> cooldownModule;
    private final Optional<DelayModule> delayModule;

    public Command(String permission, List<String> worlds, List<String> commands, Optional<BlockModule> blockModule, Optional<CooldownModule> cooldownModule, Optional<DelayModule> delayModule) {
        this.permission = permission;
        this.worlds = loadWorlds(worlds);
        this.commands = commands;
        this.blockModule = blockModule;
        this.cooldownModule = cooldownModule;
        this.delayModule = delayModule;
    }

    public boolean hasPermission(Player p) { return p.hasPermission("commandsmanager.*") || p.hasPermission(permission); }

    private List<World> loadWorlds(List<String> worlds) { return worlds.stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList()); }

    public List<World> getWorlds() { return worlds; }

    public Optional<String> getCommand(String command) {
        String[] commandArgs = command.toLowerCase().split(" ");
        return commands.stream().filter(blockedCommand -> matchesCommand(commandArgs, blockedCommand.toLowerCase().split(" "))).findFirst();
    }

    public boolean isCommand(String command) { return getCommand(command).isPresent(); }

    private boolean matchesCommand(String[] commandArgs, String[] blockedCommandArgs) { return IntStream.range(0, Math.min(blockedCommandArgs.length, commandArgs.length)).allMatch(i -> blockedCommandArgs[i].equals(commandArgs[i])) && blockedCommandArgs.length <= commandArgs.length; }

    public boolean hasBlock() { return blockModule.isPresent(); }

    public boolean hasCooldown() { return cooldownModule.isPresent(); }

    public boolean hasDelay() { return delayModule.isPresent(); }

    public BlockModule getBlockModule() { return blockModule.orElse(null); }

    public CooldownModule getCooldownModule() { return cooldownModule.orElse(null); }

    public DelayModule getDelayModule() { return delayModule.orElse(null); }

}

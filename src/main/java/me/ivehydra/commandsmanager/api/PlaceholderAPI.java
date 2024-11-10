package me.ivehydra.commandsmanager.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.command.Command;
import me.ivehydra.commandsmanager.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPI extends PlaceholderExpansion {

    private final CommandsManager instance = CommandsManager.getInstance();

    @Override
    public boolean persist() { return true; }

    @Override
    public boolean canRegister() { return true; }

    @Override
    public @NotNull String getIdentifier() { return "commandsmanager"; }

    @Override
    public @NotNull String getAuthor() { return "IVEHydra"; }

    @Override
    public @NotNull String getVersion() { return instance.getDescription().getVersion(); }

    @Override
    public @Nullable String onPlaceholderRequest(Player p, @NotNull String identifier) {
        if(identifier.startsWith("cooldown_")) {
            String string = identifier.replace("cooldown_", "");
            Command command = instance.getCommandManager().getCommandByIdentifier(string);
            if(command != null) {
                if(!command.getWorlds().contains(p.getWorld()) || command.hasPermission(p)) return MessageUtils.INACTIVE_COOLDOWN.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString());
                String cooldown = instance.getCooldownManager().getFormattedCooldown(p, string, command);
                if(!cooldown.equals("0")) return MessageUtils.ACTIVE_COOLDOWN.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString(), "%command_cooldown%", cooldown);
                return MessageUtils.INACTIVE_COOLDOWN.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString());
            }
        }
        return null;
    }

}

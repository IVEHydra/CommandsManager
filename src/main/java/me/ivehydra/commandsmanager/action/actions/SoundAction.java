package me.ivehydra.commandsmanager.action.actions;

import com.cryptomorin.xseries.XSound;
import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.action.Action;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;

public class SoundAction implements Action {

    private final CommandsManager instance = CommandsManager.getInstance();

    @Override
    public String getName() { return "SOUND"; }

    @Override
    public void execute(Player p, String string) {
        String[] args = string.split(";");
        double volume;
        double pitch;

        try {
            volume = Integer.parseInt(args[1]);
            pitch = Integer.parseInt(args[2]);
        } catch(NumberFormatException e) {
            volume = 1.0;
            pitch = 1.0;
        }

        Optional<XSound> sound = XSound.matchXSound(args[0]);
        if(!sound.isPresent()) return;
        try {
            p.playSound(p.getLocation(), Objects.requireNonNull(sound.get().parseSound()), (float) volume, (float) pitch);
        } catch(Exception e) {
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " An error occurred while trying to play the sound. Ensure that the sound name is correct and supported by the server.");
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
        }
    }

}

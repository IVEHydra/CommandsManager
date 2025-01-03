package me.ivehydra.commandsmanager.action.actions;

import com.cryptomorin.xseries.XSound;
import me.ivehydra.commandsmanager.CommandsManager;
import me.ivehydra.commandsmanager.action.Action;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

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

        Optional<XSound> xSound = XSound.of(args[0]);

        if(!xSound.isPresent()) {
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Sound not found: " + args[0]);
            return;
        }

        try {
            Sound sound = xSound.get().get();

            if(sound == null) {
                instance.sendLog("[CommandsManager]" + ChatColor.RED + " Invalid Sound: " + args[0]);
                return;
            }

            p.playSound(p.getLocation(), sound, (float) volume, (float) pitch);
        } catch(Exception e) {
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " An error occurred while trying to play the sound. Ensure that the sound name is correct and supported by the server.");
            instance.sendLog("[CommandsManager]" + ChatColor.RED + " Error details: " + e.getMessage());
        }
    }

}

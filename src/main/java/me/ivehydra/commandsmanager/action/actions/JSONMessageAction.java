package me.ivehydra.commandsmanager.action.actions;

import me.ivehydra.commandsmanager.action.Action;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;

public class JSONMessageAction implements Action {

    @Override
    public String getName() { return "JSON_MESSAGE"; }

    @Override
    public void execute(Player p, String string) {
        BaseComponent[] base = ComponentSerializer.parse(string);
        p.spigot().sendMessage(base);
    }
}

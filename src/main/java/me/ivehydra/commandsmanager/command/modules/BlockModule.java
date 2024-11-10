package me.ivehydra.commandsmanager.command.modules;

import java.util.List;

public class BlockModule {

    private final List<String> actions;

    public BlockModule(List<String> actions) { this.actions = actions; }

    public List<String> getActions() { return actions; }

}

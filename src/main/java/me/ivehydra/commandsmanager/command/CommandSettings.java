package me.ivehydra.commandsmanager.command;

public class CommandSettings {

    private final boolean colons;
    private final boolean command;
    private final boolean chat;
    private final boolean move;
    private final boolean interact;
    private final boolean damage;

    public CommandSettings(boolean colons, boolean command, boolean chat, boolean move, boolean interact, boolean damage) {
        this.colons = colons;
        this.command = command;
        this.chat = chat;
        this.move = move;
        this.interact = interact;
        this.damage = damage;
    }

    public boolean isColons() { return colons; }

    public boolean isCommand() { return command; }

    public boolean isChat() { return chat; }

    public boolean isMove() { return move; }

    public boolean isInteract() { return interact; }

    public boolean isDamage() { return damage; }

}

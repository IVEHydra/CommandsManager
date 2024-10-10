package me.ivehydra.commandsmanager.utils;

import me.ivehydra.commandsmanager.CommandsManager;

public enum MessageUtils {

    PREFIX("messages.prefix"),
    NO_PERMISSION("messages.general.noPermission"),
    CONFIG_RELOADED("messages.general.configReloaded"),
    WRONG_ARGUMENTS("messages.general.wrongArguments"),
    LATEST_VERSION("messages.updateCheck.latestVersion"),
    NEW_VERSION("messages.updateCheck.newVersionAvailable"),
    BLOCK_COLONS("messages.command.blockColons"),
    NO_EXPERIENCE("messages.command.cost.noEXP"),
    NO_MONEY("messages.command.cost.noMoney"),
    NO_CUSTOM("messages.command.cost.noCustom"),
    SECONDS("messages.time.seconds"),
    MINUTES("messages.time.minutes"),
    HOURS("messages.time.hours"),
    DAYS("messages.time.days"),
    ACTIVE_COOLDOWN("messages.placeholder.cooldown.activeCooldown"),
    INACTIVE_COOLDOWN("messages.placeholder.cooldown.inactiveCooldown");

    private final CommandsManager instance = CommandsManager.getInstance();
    private final String path;

    MessageUtils(String path) { this.path = path; }

    public String getPath() { return path; }

    public String getFormattedMessage(Object... replacements) {
        String message = instance.getConfig().getString(path);
        message = StringUtils.getColoredString(message);
        for(int i = 0; i < replacements.length; i += 2) {
            String placeholder = (String) replacements[i];
            String value = (String) replacements[i + 1];
            message = message.replace(placeholder, value);
        }
        return message;
    }

    @Override
    public String toString() { return getFormattedMessage(); }

}

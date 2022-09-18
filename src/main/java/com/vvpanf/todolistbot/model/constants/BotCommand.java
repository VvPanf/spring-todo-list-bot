package com.vvpanf.todolistbot.model.constants;

public enum BotCommand {
    START("/start"),
    HELP("/help"),
    SHOW("/show"),
    CREATE("/create"),
    DELETE("/delete");

    private final String command;

    BotCommand(String command) {
        this.command = command;
    }

    public static BotCommand parse(String command) {
        for (BotCommand c : BotCommand.values()) {
            if (c.command.equalsIgnoreCase(command)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.command;
    }
}

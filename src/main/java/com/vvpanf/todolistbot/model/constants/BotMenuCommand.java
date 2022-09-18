package com.vvpanf.todolistbot.model.constants;

public enum BotMenuCommand {
    CLOSE("close"),
    NEXT("next"),
    PREV("prev");

    private final String command;

    BotMenuCommand(String command) {
        this.command = command;
    }

    public static BotMenuCommand parse(String command) {
        for (BotMenuCommand c : BotMenuCommand.values()) {
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

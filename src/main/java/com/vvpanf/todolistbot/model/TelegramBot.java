package com.vvpanf.todolistbot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class TelegramBot extends SpringWebhookBot {
    String botPath;
    String botUsername;
    String botToken;

    private TelegramFacade telegramFacade;

    public TelegramBot(TelegramFacade telegramFacade, DefaultBotOptions options, SetWebhook setWebhook) {
        super(options, setWebhook);
        this.telegramFacade = telegramFacade;
    }

    public TelegramBot(TelegramFacade telegramFacade, SetWebhook setWebhook) {
        super(setWebhook);
        this.telegramFacade = telegramFacade;
    }

    public void registerCommands() throws TelegramApiException {
        try {
            log.debug("Starting register bot commands");
            List<BotCommand> commands = List.of(
                    new BotCommand("show", "посмотреть свои списки"),
                    new BotCommand("create", "создать новый список"),
                    new BotCommand("delete", "удалить список"),
                    new BotCommand("help", "справка")
            );
            SetMyCommands cmds = new SetMyCommands(commands, new BotCommandScopeDefault(),  "ru");
            Boolean result = executeAsync(cmds).get();
            log.debug("Bot commands registered: {}", result);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Queue<BotApiMethod<?>> methods = new ArrayDeque<>(telegramFacade.handleUpdate(update));
        if (methods.isEmpty()) {
            return null;
        }
        while (methods.size() > 1) {
            try {
                executeAsync(methods.poll());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        return methods.poll();
    }
}

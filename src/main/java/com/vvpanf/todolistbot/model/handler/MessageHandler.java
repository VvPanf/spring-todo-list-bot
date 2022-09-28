package com.vvpanf.todolistbot.model.handler;

import com.vvpanf.todolistbot.model.BotStateCash;
import com.vvpanf.todolistbot.model.constants.BotCommand;
import com.vvpanf.todolistbot.model.constants.BotState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageHandler {
    final EventHandler eventHandler;
    final BotStateCash botStateCash;

    public List<BotApiMethod<?>> handle(Message message) {
        BotState botState = botStateCash.getBotState(message.getFrom().getId()) == null
                ? BotState.MAIN
                : botStateCash.getBotState(message.getFrom().getId());
        if (botState == BotState.MAIN) {
            return handleCommand(message);
        }
        return handleState(message, botState);
    }

    private List<BotApiMethod<?>> handleCommand(Message message) {
        final long chatId = message.getChatId();
        final long userId = message.getFrom().getId();
        final BotCommand botCommand = BotCommand.parse(message.getText());

        if (botCommand == null) {
            return eventHandler.unknownMessage(chatId);
        }

        switch (botCommand) {
            case START:
                return eventHandler.startMessage(chatId, userId);
            case HELP:
                return eventHandler.helpMessage(chatId);
            case SHOW:
                return eventHandler.showListsMessage(chatId, userId);
            case CREATE:
                return eventHandler.createListMessage(chatId, userId);
            case DELETE:
                return eventHandler.deleteListMessage(chatId, userId);
            default:
                throw new RuntimeException("Unknown bot command");
        }
    }

    private List<BotApiMethod<?>> handleState(Message message, BotState botState) {
        final long chatId = message.getChatId();
        final long userId = message.getFrom().getId();
        final String text = message.getText();

        switch (botState) {
            case ENTER_LIST:
                return eventHandler.enterListMessage(chatId, userId, text);
            default:
                throw new RuntimeException("Unknown bot state");
        }
    }
}

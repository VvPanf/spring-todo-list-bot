package com.vvpanf.todolistbot.model.handler;

import com.vvpanf.todolistbot.model.BotStateCash;
import com.vvpanf.todolistbot.model.constants.BotMenuCommand;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallbackQueryHandler {
    final EventHandler eventHandler;
    final BotStateCash botStateCash;

    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        final Integer messageId = callbackQuery.getMessage().getMessageId();
        final String messageText = callbackQuery.getMessage().getText();
        final long userId = callbackQuery.getFrom().getId();
        final String callbackId = callbackQuery.getId();
        final String data = callbackQuery.getData();

        if (BotMenuCommand.CLOSE.toString().equals(data)) {
            return eventHandler.closeCallback(chatId, messageId, callbackId);
        }

        if (BotMenuCommand.PREV.toString().equals(data)) {
            return eventHandler.prevCallback(chatId, userId, messageId, messageText, callbackId);
        }

        if (BotMenuCommand.NEXT.toString().equals(data)) {
            return eventHandler.nextCallback(chatId, userId, messageId, messageText, callbackId);
        }

        if (data.startsWith("list")) {
            long listId = Long.parseLong(data.replace("list", ""));
            return eventHandler.showListItemsCallback(chatId, userId, messageId, listId);
        }

        if (data.startsWith("item")) {
            long itemId = Long.parseLong(data.replace("item", ""));
            return eventHandler.toggleItemCallback(chatId, userId, messageId, messageText, itemId, callbackId);
        }

        if (data.startsWith("delete")) {
            long deleteId = Long.parseLong(data.replace("delete", ""));
            return eventHandler.deleteListCallback(chatId, userId, messageId, deleteId);
        }

        return null;
    }
}

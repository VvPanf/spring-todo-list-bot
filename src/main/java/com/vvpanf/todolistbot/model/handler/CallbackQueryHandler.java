package com.vvpanf.todolistbot.model.handler;

import com.vvpanf.todolistbot.model.BotStateCash;
import com.vvpanf.todolistbot.model.constants.BotMenuCommand;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.JSONException;
import org.json.JSONObject;
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

        try {
            JSONObject json = new JSONObject(data);
            if (json.has("listId")) {
                String listId = json.getString("listId");
                return eventHandler.showListItemsCallback(chatId, userId, messageId, listId, callbackId);
            }

            if (json.has("itemId")) {
                String itemId = json.getString("itemId");
                return eventHandler.toggleItemCallback(chatId, userId, messageId, messageText, itemId, callbackId);
            }

            if (json.has("deleteId")) {
                String deleteId = json.getString("deleteId");
                return eventHandler.deleteListCallback(chatId, userId, messageId, deleteId, callbackId);
            }
        } catch (JSONException exception) { exception.printStackTrace(); }

        return null;
    }
}

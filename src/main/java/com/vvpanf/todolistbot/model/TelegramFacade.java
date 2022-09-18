package com.vvpanf.todolistbot.model;

import com.vvpanf.todolistbot.model.handler.CallbackQueryHandler;
import com.vvpanf.todolistbot.model.handler.MessageHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramFacade {
    final MessageHandler messageHandler;
    final CallbackQueryHandler callbackQueryHandler;

    public Collection<BotApiMethod<?>> handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.handle(callbackQuery);
        } else {
            Message message = update.getMessage();
            if (message != null && message.hasText()) {
                return messageHandler.handle(message);
            }
        }
        return null;
    }
}

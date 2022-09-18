package com.vvpanf.todolistbot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBotConfig {
    @Value("${telegrambot.webhookPath}")
    String webHookPath;
    @Value("${telegrambot.botUsername}")
    String userName;
    @Value("${telegrambot.botToken}")
    String botToken;
}

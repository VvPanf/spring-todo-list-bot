package com.vvpanf.todolistbot.config;

import com.vvpanf.todolistbot.model.TelegramBot;
import com.vvpanf.todolistbot.model.TelegramFacade;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
@AllArgsConstructor
public class AppConfig {
    private final TelegramBotConfig botConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botConfig.getWebHookUrl()).build();
    }

    @Bean
    public TelegramBot springWebhookBot(SetWebhook setWebhook, TelegramFacade telegramFacade) throws TelegramApiException {
        TelegramBot bot = new TelegramBot(telegramFacade, setWebhook);
        bot.setBotUsername(botConfig.getUserName());
        bot.setBotToken(botConfig.getBotToken());
        bot.setBotPath(botConfig.getUserName());
        bot.registerBot();
        bot.registerCommands();
        return bot;
    }
}

package com.vvpanf.todolistbot.config;

import com.vvpanf.todolistbot.model.TelegramBot;
import com.vvpanf.todolistbot.model.TelegramFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@Configuration
@AllArgsConstructor
@Slf4j
public class AppConfig {
    private final TelegramBotConfig botConfig;

    @Bean
    public SetWebhook setWebhookInstance() throws IOException {
        log.debug("Setting the Webhook Instance");
        return SetWebhook.builder()
                .url(botConfig.getWebHookUrl())
                .build();
    }

    @Bean
    public TelegramBot springWebhookBot(SetWebhook setWebhook, TelegramFacade telegramFacade) throws TelegramApiException {
        log.debug("Starting setup bot");
        TelegramBot bot = new TelegramBot(telegramFacade, setWebhook);
        bot.setBotUsername(botConfig.getUserName());
        bot.setBotToken(botConfig.getBotToken());
        bot.setBotPath(botConfig.getBotPath());
        // Register bot
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class, new ServerlessWebhook());
//        telegramBotsApi.registerBot(bot, setWebhook);
        bot.registerCommands();
        return bot;
    }
}

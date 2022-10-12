package com.vvpanf.todolistbot.config;

import com.vvpanf.todolistbot.model.TelegramBot;
import com.vvpanf.todolistbot.model.TelegramFacade;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Configuration
@AllArgsConstructor
public class AppConfig {
    private final TelegramBotConfig botConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botConfig.getWebHookPath()).build();
    }

    @Bean
    public TelegramBot springWebhookBot(SetWebhook setWebhook, TelegramFacade telegramFacade) throws TelegramApiException {
        TelegramBot bot = new TelegramBot(telegramFacade, setWebhook);
        bot.setBotToken(botConfig.getBotToken());
        bot.setBotUsername(botConfig.getUserName());
        bot.setBotPath(botConfig.getWebHookPath());
        List<BotCommand> commands = List.of(
            new BotCommand("show", "посмотреть свои списки"),
            new BotCommand("create", "создать новый список"),
            new BotCommand("delete", "удалить список"),
            new BotCommand("help", "справка")
        );
        SetMyCommands cmds = new SetMyCommands(commands, new BotCommandScopeDefault(),  "ru");
        bot.execute(cmds);
        return bot;
    }
}

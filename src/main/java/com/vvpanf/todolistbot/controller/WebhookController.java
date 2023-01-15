package com.vvpanf.todolistbot.controller;

import com.vvpanf.todolistbot.model.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/todo-list-bot")
@AllArgsConstructor
@Slf4j
public class WebhookController {
    private final TelegramBot telegramBot;

    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        log.debug("New update received");
        return telegramBot.onWebhookUpdateReceived(update);
    }

    @GetMapping
    public ResponseEntity<?> get() {
        log.debug("Get request to server");
        return ResponseEntity.ok().build();
    }
}

package com.vvpanf.todolistbot.model;

import com.vvpanf.todolistbot.model.constants.BotState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BotStateCash {
    private final Map<Long, BotState> botStateMap = new HashMap<>();

    public void saveBotState(long userId, BotState botState) {
        botStateMap.put(userId, botState);
    }

    public BotState getBotState(long userId) {
        return botStateMap.get(userId);
    }
}

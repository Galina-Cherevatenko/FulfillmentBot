package com.example.fulbot.cache;

import com.example.fulbot.botapi.BotState;
import com.example.fulbot.entities.Calculation;

public interface DataCache {
    void setUsersCurrentBotState(long chatId, BotState botState);

    BotState getUsersCurrentBotState (long chatId);
    Calculation getUserCalculation(long chatId);
    void saveUserCalculation (long chatId, Calculation calculation);
}
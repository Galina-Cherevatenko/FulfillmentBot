package com.example.fulbot.cache;

import com.example.fulbot.botapi.BotState;
import com.example.fulbot.entities.Calculation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache implements DataCache{
    private Map<Long, BotState> usersBotState = new HashMap<>();
    private Map<Long, Calculation> usersCalculations = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(long chatId, BotState botState) {
        usersBotState.put(chatId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(long chatId) {
        BotState botState = usersBotState.get(chatId);
        if (botState == null){
            botState = BotState.ASK_DELIVERY;
        }
        return botState;
    }

    @Override
    public Calculation getUserCalculation(long chatId) {
        Calculation calculation = usersCalculations.get(chatId);
        if (calculation == null){
            calculation = new Calculation();
        }
        return calculation;
    }

    @Override
    public void saveUserCalculation(long chatId, Calculation calculation) {
        usersCalculations.put(chatId, calculation);
    }

    public void deleteUserCalculation(long chatId) {
        usersCalculations.remove(chatId);
    }
}

package com.example.fulbot;

import com.example.fulbot.botapi.TelegramFacade;
import com.example.fulbot.config.BotConfig;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Component
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GynnyTelegramBot extends TelegramLongPollingBot {
    final BotConfig botConfig;
    String botUserName;
    String botToken;

    TelegramFacade telegramFacade;

    public GynnyTelegramBot(BotConfig botConfig, TelegramFacade telegramFacade) {
        this.botConfig=botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "создать новый расчет"));
        listOfCommands.add(new BotCommand("/mydata", "просмотреть мои расчеты"));
        listOfCommands.add(new BotCommand("/deletedata", "удалить мои расчеты"));
        try{
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));

        }
        catch (TelegramApiException e){
            System.out.println(e.getMessage());
        }
        this.telegramFacade = telegramFacade;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }


    @Override
    public void onUpdateReceived(Update update) {
        SendMessage replyMessageToUser = telegramFacade.handleUpdate(update);
        executeMessage(replyMessageToUser);
    }

    private void executeMessage(SendMessage message){
        try {
            execute(message);
        }
        catch (TelegramApiException e){
            System.out.println(e.getMessage());
        }
    }


}

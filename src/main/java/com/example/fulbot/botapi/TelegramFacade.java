package com.example.fulbot.botapi;

import com.example.fulbot.botapi.handlers.CallbackQueryHandler;
import com.example.fulbot.botapi.handlers.MessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramFacade {

    private MessageHandler messageHandler;
    private CallbackQueryHandler callbackQueryHandler;


    public TelegramFacade(MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler) {

        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            replyMessage = messageHandler.handleInputMessage(message);
        }

        return replyMessage;
    }

}


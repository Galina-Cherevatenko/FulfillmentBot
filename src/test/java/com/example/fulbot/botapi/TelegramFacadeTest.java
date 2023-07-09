package com.example.fulbot.botapi;

import com.example.fulbot.botapi.handlers.CallbackQueryHandler;
import com.example.fulbot.botapi.handlers.MessageHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramFacadeTest {
    @InjectMocks
    TelegramFacade telegramFacade;

    @Mock
    private MessageHandler messageHandler;

    @Mock
    private CallbackQueryHandler callbackQueryHandler;

    Message message = mock(Message.class);

    Update update = mock(Update.class);

    CallbackQuery callbackQuery = new CallbackQuery();

    final long chatId = 1L;

    @Test
    void testHandleUpdateForMessage() {

        SendMessage callBackAnswer = new SendMessage(String.valueOf(chatId),"Введите объем поставки в шт.:");

        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(messageHandler.handleInputMessage(message)).thenReturn(callBackAnswer);

        SendMessage resultAnswer = telegramFacade.handleUpdate(update);

        assertNotNull(resultAnswer);
        assertEquals(callBackAnswer, resultAnswer);
    }

    @Test
    void testHandleUpdateForCallBackQuery() {
        SendMessage callBackAnswer = new SendMessage(String.valueOf(chatId), "Введите номер телефона:");

        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(callbackQueryHandler.processCallbackQuery(callbackQuery)).thenReturn(callBackAnswer);

        SendMessage resultAnswer = telegramFacade.handleUpdate(update);

        assertNotNull(resultAnswer);
        assertEquals(callBackAnswer, resultAnswer);
    }
}
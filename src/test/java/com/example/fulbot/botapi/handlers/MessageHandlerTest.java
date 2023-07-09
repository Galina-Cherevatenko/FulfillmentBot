package com.example.fulbot.botapi.handlers;

import com.example.fulbot.botapi.BotState;
import com.example.fulbot.cache.UserDataCache;
import com.example.fulbot.entities.Calculation;
import com.example.fulbot.entities.User;
import com.example.fulbot.repositories.UserRepository;
import com.example.fulbot.services.BitrixService;
import com.example.fulbot.services.CalculationService;
import com.example.fulbot.services.KeyboardMarkupMaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageHandlerTest {
    @InjectMocks
    private MessageHandler messageHandler;

    @Mock
    UserDataCache userDataCache;

    @Mock
    KeyboardMarkupMaker keyboardMarkupMaker;

    @Mock
    CalculationService calculationService;

    @Mock
    UserRepository userRepository;

    @Mock
    BitrixService bitrixService;

    final long chatId = 1L;

    Message message = mock(Message.class);
    User user = new User(1L, "Galina", "Cherevatenko",
            "Galina", new Timestamp(System.currentTimeMillis()), "0000000000", chatId);
    Calculation calculation = new Calculation
            (1L, 10, 10, false, false, false, false,
                    false, false, false, 10, 0, user);

    @Test
    void testAnswerForBoxQuantityInput() {
        String text = "123";
        BotState botState = BotState.ASK_BOX_QUANTITY;
        SendMessage callBackAnswer = new SendMessage(String.valueOf(chatId), "Введите объем поставки в шт.:");

        when(message.getText()).thenReturn(text);
        when(message.getChatId()).thenReturn(chatId);
        when(userDataCache.getUsersCurrentBotState(anyLong())).thenReturn(botState);
        when(userDataCache.getUserCalculation(anyLong())).thenReturn(calculation);
        when(userRepository.findByChatId(anyLong())).thenReturn(Optional.of(user));


        SendMessage resultAnswer = messageHandler.handleInputMessage(message);


        assertNotNull(resultAnswer);
        assertEquals(calculation.getBoxQuantity(), 123);
        assertEquals(callBackAnswer, resultAnswer);
    }

    @Test
    void testAnswerForIncorrectBoxQuantityInput() {
        String text = "dfg";
        BotState botState = BotState.ASK_BOX_QUANTITY;
        SendMessage callBackAnswer = new SendMessage(String.valueOf(chatId),
                "Количество коробок должно быть числом больше 0. Повторите ввод:");

        when(message.getText()).thenReturn(text);
        when(message.getChatId()).thenReturn(chatId);
        when(userDataCache.getUsersCurrentBotState(anyLong())).thenReturn(botState);
        when(userDataCache.getUserCalculation(anyLong())).thenReturn(calculation);
        when(userRepository.findByChatId(anyLong())).thenReturn(Optional.of(user));


        SendMessage resultAnswer = messageHandler.handleInputMessage(message);


        assertNotNull(resultAnswer);
        assertEquals(callBackAnswer, resultAnswer);
    }

    @Test
    void testAnswerForPhoneInput() {
        String text = "89515678900";
        BotState botState = BotState.ASK_PHONE;
        SendMessage callBackAnswer = new SendMessage(String.valueOf(chatId),
                "Благодарю за обращение, с вами свяжутся в ближайшее время.");

        when(message.getText()).thenReturn(text);
        when(message.getChatId()).thenReturn(chatId);
        when(userDataCache.getUsersCurrentBotState(anyLong())).thenReturn(botState);
        when(userDataCache.getUserCalculation(anyLong())).thenReturn(calculation);
        when(userRepository.findByChatId(anyLong())).thenReturn(Optional.of(user));


        SendMessage resultAnswer = messageHandler.handleInputMessage(message);

        assertNotNull(resultAnswer);
        assertEquals(user.getPhone(), "89515678900");
        assertEquals(callBackAnswer, resultAnswer);
    }

    @Test
    void testAnswerForIncorrectPhoneInput() {
        String text = "cghgdh";
        BotState botState = BotState.ASK_PHONE;
        SendMessage callBackAnswer = new SendMessage(String.valueOf(chatId),
                "Введите корректный номер телефона или выберите дальнейшее действие:");

        when(message.getText()).thenReturn(text);
        when(message.getChatId()).thenReturn(chatId);
        when(userDataCache.getUsersCurrentBotState(anyLong())).thenReturn(botState);
        when(userDataCache.getUserCalculation(anyLong())).thenReturn(calculation);
        when(userRepository.findByChatId(anyLong())).thenReturn(Optional.of(user));

        SendMessage resultAnswer = messageHandler.handleInputMessage(message);

        assertNotNull(resultAnswer);
        assertEquals(callBackAnswer.getText(), resultAnswer.getText());
    }
}
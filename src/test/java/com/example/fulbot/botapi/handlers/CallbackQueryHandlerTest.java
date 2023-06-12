package com.example.fulbot.botapi.handlers;

import com.example.fulbot.botapi.BotState;
import com.example.fulbot.cache.UserDataCache;
import com.example.fulbot.entities.Calculation;
import com.example.fulbot.entities.User;
import com.example.fulbot.repositories.CalculationRepository;
import com.example.fulbot.repositories.UserRepository;
import com.example.fulbot.services.KeyboardMarkupMaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CallbackQueryHandlerTest {

    @Autowired
    private CallbackQueryHandler callbackQueryHandler;

    @MockBean
    UserDataCache userDataCache;

    @Autowired
    KeyboardMarkupMaker keyboardMarkupMaker;

    @MockBean
    CalculationRepository calculationRepository;

    @MockBean
    UserRepository userRepository;


        final long chatId = 1L;

        Message message = mock(Message.class);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);

        User user = new User(1L, "Galina", "Cherevatenko",
                "Galina",new Timestamp(System.currentTimeMillis()), "1234567890", chatId);
        Calculation calculation = new Calculation
                (1L, 10, 10, false, false,false, false,
                        false, false, false, 10, 0, user);



    @Test
     void testAnswerForDefectiveCallbackQuery() {
        String data = "Отбраковка";
        SendMessage callBackAnswer = new SendMessage(String.valueOf(chatId),"Отбраковка простая или сложная?");
        callBackAnswer.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                "Простая", "Простая", "Сложная", "Сложная"));

        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
        when(callbackQuery.getData()).thenReturn(data);
        when(userDataCache.getUserCalculation(anyLong())).thenReturn(calculation);
        when(userRepository.findByChatId(anyLong())).thenReturn(Optional.of(user));
        when(userDataCache.getUsersCurrentBotState(anyLong())).thenReturn(BotState.ASK_DEFECTIVE);

        SendMessage resultAnswer = callbackQueryHandler.processCallbackQuery(callbackQuery);


        assertNotNull(resultAnswer);
        assertTrue(calculation.isDefective());
        assertEquals(callBackAnswer, resultAnswer);
        }

    @Test
    void testAnswerForDoNotPrepareBoxesCallbackQuery() {
        String data = "Не готовить коробы";
        calculation.setTotalPrice(610);
        SendMessage callBackAnswer = new SendMessage(String.valueOf(chatId),
                "Итоговая цена: "+calculation.getTotalPrice()+" рублей. \n"+"Мне все подходит, свяжитесь со мной.");
        callBackAnswer.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                "Да", "Ввод телефона", "Нет", "Отбой"));

        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
        when(callbackQuery.getData()).thenReturn(data);
        when(userDataCache.getUserCalculation(anyLong())).thenReturn(calculation);
        when(userRepository.findByChatId(anyLong())).thenReturn(Optional.of(user));
        when(userDataCache.getUsersCurrentBotState(anyLong())).thenReturn(BotState.ASK_PREPARE_BOXES);

        SendMessage resultAnswer = callbackQueryHandler.processCallbackQuery(callbackQuery);


        assertNotNull(resultAnswer);
        assertFalse(calculation.isPrepareBoxes());
        assertEquals(callBackAnswer, resultAnswer);
    }

    @Test
    void testAnswerForIncorrectBotState() {
        String data = "Отбраковка";
        SendMessage callBackAnswer = new SendMessage(String.valueOf(chatId),
                "Я так не умею. Начните заново с команды /start");

        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);
        when(callbackQuery.getData()).thenReturn(data);
        when(userDataCache.getUserCalculation(anyLong())).thenReturn(calculation);
        when(userRepository.findByChatId(anyLong())).thenReturn(Optional.of(user));
        when(userDataCache.getUsersCurrentBotState(anyLong())).thenReturn(BotState.ASK_PREPARE_BOXES);

        SendMessage resultAnswer = callbackQueryHandler.processCallbackQuery(callbackQuery);


        assertNotNull(resultAnswer);
        assertEquals(callBackAnswer, resultAnswer);
    }
}
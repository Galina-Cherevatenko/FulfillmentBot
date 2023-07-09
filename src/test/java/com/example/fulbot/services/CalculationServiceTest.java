package com.example.fulbot.services;

import com.example.fulbot.entities.Calculation;
import com.example.fulbot.entities.User;
import com.example.fulbot.repositories.CalculationRepository;
import com.example.fulbot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @InjectMocks
    CalculationService calculationService;

    @Mock
    CalculationRepository calculationRepository;

    @Mock
    UserRepository userRepository;

    final long chatId = 1L;
    Message message = mock(Message.class);
    User user1 = new User(1L, "Galina", "Cherevatenko",
            "Galina",new Timestamp(System.currentTimeMillis()), "1234567890", 1L);
    User user2 = new User(2L, "Polina", "Cherevatenko",
            "Polina",new Timestamp(System.currentTimeMillis()), "1234567890", 2L);
    Calculation calculation1 = new Calculation
            ( 1L, 10, 10, false, false,false, false,
                    false, false, false, 10, 610, user1);
    Calculation calculation2 = new Calculation
            (2L, 20, 20, false, false,false, false,
                    false, false, false, 10, 810, user1);
    Calculation calculation3 = new Calculation
            (3L, 30, 30, false, false,false, false,
                    false, false, false, 10, 1010, user2);
    @Test
    void findAllByUser() {
        List<Calculation> calculations = new ArrayList<>(Arrays.asList(calculation1, calculation2));

        when(message.getChatId()).thenReturn(chatId);
        when(userRepository.findByChatId(chatId)).thenReturn(Optional.of(user1));
        when(calculationRepository.findByUser(user1)).thenReturn(calculations);

        List<Calculation> resultList = calculationService.findAllByUser(message);

        assertNotNull(resultList);
        assertEquals(calculations.size(), resultList.size());
        assertEquals(calculations.get(0).getUser().getUserId(), resultList.get(0).getUser().getUserId());
    }

}
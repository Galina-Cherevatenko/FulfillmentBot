package com.example.fulbot.services;

import com.example.fulbot.entities.Calculation;
import com.example.fulbot.exceptions.UserNotFoundException;
import com.example.gynnybotwebhook.repositories.CalculationRepository;
import com.example.gynnybotwebhook.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CalculationService {

    CalculationRepository calculationRepository;
    UserRepository userRepository;


    public List<Calculation> findAllByUser (Message msg){

        List<Calculation> userCalculations = calculationRepository
                .findByUser(userRepository.findByChatId(msg.getChatId()).orElseThrow(UserNotFoundException::new));
        return userCalculations;
    }
    @Transactional
    public void deleteAllByUser (Message msg){
        List<Calculation> userCalculations = findAllByUser(msg);
        for(Calculation calculation:userCalculations) {
            calculationRepository.deleteById(calculation.getId());
        }
    }
}

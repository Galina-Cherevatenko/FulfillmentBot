package com.example.fulbot.botapi.handlers;

import com.example.fulbot.botapi.BotState;
import com.example.fulbot.entities.Calculation;
import com.example.fulbot.entities.User;
import com.example.fulbot.exceptions.UserNotFoundException;
import com.example.fulbot.services.BitrixService;
import com.example.fulbot.services.CalculationService;
import com.example.fulbot.services.KeyboardMarkupMaker;
import com.example.fulbot.cache.UserDataCache;
import com.example.fulbot.repositories.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    UserDataCache userDataCache;
    KeyboardMarkupMaker keyboardMarkupMaker;
    CalculationService calculationService;
    UserRepository userRepository;

    BitrixService bitrixService;

    public SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;
        registerUser(message);

        switch (inputMsg) {
            case "/start":
                botState = BotState.ASK_DELIVERY;
                userDataCache.deleteUserCalculation(chatId);
                break;
            case "/mydata":
                botState = BotState.SHOW_CALCULATIONS;
                break;
            case "/deletedata":
                userDataCache.deleteUserCalculation(chatId);
                calculationService.deleteAllByUser(message);
                botState = BotState.DELETE_CALCULATIONS;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(chatId);
                break;
        }

        userDataCache.setUsersCurrentBotState(chatId, botState);

        replyMessage = answerMessage(botState, message);

        return replyMessage;
    }

    private SendMessage showCalculations(Message msg) {
        SendMessage userReply;
        var chatId = msg.getChatId();
        StringBuilder stringBuilder = new StringBuilder();
        List<Calculation> calculations = calculationService.findAllByUser(msg);
        if (calculations.size()>0){
            for(Calculation calculation:calculations){
                stringBuilder.append(String.format("%s%n--------------------%n%s", "Расчет:", calculation.toString()));}
        } else {
            stringBuilder.append("У вас нет готовых расчетов.");
        }
        userReply=new SendMessage(String.valueOf(chatId), stringBuilder.toString());
        return userReply;

    }

    private void registerUser(Message msg){
        if (userRepository.findByChatId(msg.getChatId()).isEmpty()){
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);

        }
    }
    private SendMessage answerMessage (BotState botState, Message inputMsg) {
        String usersAnswer = inputMsg.getText();

        long chatId = inputMsg.getChatId();

        Calculation calculation = userDataCache.getUserCalculation(chatId);
        User user = userRepository.findByChatId(chatId).orElseThrow(UserNotFoundException::new);

        SendMessage replyToUser = null;

        switch (botState) {
            case ASK_DELIVERY:
                System.out.println(calculation.toString());
                replyToUser = new SendMessage(String.valueOf(chatId),"Привет, "+inputMsg.getChat().getFirstName()+
                        "!"+ "\n"+"Нужно ли забрать груз?");
                replyToUser.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                        "Да", "Забрать", "Нет", "Привезет"));
                break;
            case ASK_BOX_QUANTITY:
                int boxQuantity=0;
                try {
                   boxQuantity = Integer.parseInt(usersAnswer);
                   calculation.setBoxQuantity(boxQuantity);
                    if (boxQuantity>0){
                        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_ITEM_QUANTITY);
                        replyToUser = new SendMessage(String.valueOf(chatId),"Введите объем поставки в шт.:");}
                    else {
                        replyToUser = new SendMessage(String.valueOf(chatId),
                                "Количество коробок должно быть числом больше 0. Повторите ввод:");
                    }
                } catch (NumberFormatException e) {
                    replyToUser = new SendMessage(String.valueOf(chatId),
                            "Количество коробок должно быть числом больше 0. Повторите ввод:");
                }
                break;
            case ASK_ITEM_QUANTITY:
                int itemQuantity=0;
                try {
                    itemQuantity = Integer.parseInt(usersAnswer);
                    calculation.setItemQuantity(itemQuantity);
                    if (itemQuantity > 0) {
                        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_DEFECTIVE);
                        replyToUser = new SendMessage(String.valueOf(chatId), "Потребуется ли отбраковка?");
                        replyToUser.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                                "Да", "Отбраковка", "Нет", "Не отбраковывать"));
                    }
                    else{
                            replyToUser = new SendMessage(String.valueOf(chatId),
                                    "Количество штук должно быть числом больше 0. Повторите ввод:");
                        }
                    } catch(NumberFormatException e){
                        replyToUser = new SendMessage(String.valueOf(chatId),
                                "Количество штук должно быть числом больше 0. Повторите ввод:");
                    }

                break;
            case ASK_PHONE:
                ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
                Validator validator = validatorFactory.getValidator();
                user.setPhone(usersAnswer);
                Set<ConstraintViolation<User>> errors = validator.validate(user);
                StringBuilder stringBuilder = new StringBuilder();
                for(ConstraintViolation<User> error: errors){
                    stringBuilder.append(error.getMessage());
                }
                String errorMessage = stringBuilder.toString();
                if (errorMessage.isEmpty()){
                    userRepository.save(user);
                    userDataCache.setUsersCurrentBotState(chatId, BotState.FINISH);
                    try {
                        bitrixService.postLead(chatId, calculation);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                    replyToUser = new SendMessage(String.valueOf(chatId),
                            "Благодарю за обращение, с вами свяжутся в ближайшее время.");
                    userDataCache.deleteUserCalculation(chatId);

                }
                    else {
                        replyToUser = new SendMessage(String.valueOf(chatId), errorMessage +
                                "Повторите ввод или выберите дальнейшее действие:");
                        replyToUser.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                            "Начать расчет заново", "Начало", "Отбой", "Отбой"));
                }

                break;
            case SHOW_CALCULATIONS:
                replyToUser = showCalculations(inputMsg);
                break;
            case DELETE_CALCULATIONS:
                replyToUser = new SendMessage(String.valueOf(chatId),
                        "Ваши расчеты удалены.");
                break;
            default:

                break;
        }

        userDataCache.saveUserCalculation(chatId, calculation);
        return replyToUser;
    }


}

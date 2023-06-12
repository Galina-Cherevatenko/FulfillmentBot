package com.example.fulbot.botapi.handlers;

import com.example.fulbot.botapi.BotState;
import com.example.fulbot.entities.Calculation;
import com.example.fulbot.entities.User;
import com.example.fulbot.exceptions.UserNotFoundException;
import com.example.fulbot.services.KeyboardMarkupMaker;
import com.example.fulbot.cache.UserDataCache;
import com.example.fulbot.repositories.CalculationRepository;
import com.example.fulbot.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CallbackQueryHandler {
    UserDataCache userDataCache;
    KeyboardMarkupMaker keyboardMarkupMaker;
    CalculationRepository calculationRepository;
    UserRepository userRepository;

    public SendMessage processCallbackQuery(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        SendMessage callBackAnswer = new SendMessage(String.valueOf(chatId),
                "Я так не умею. Начните заново с команды /start");
        Calculation calculation = userDataCache.getUserCalculation(chatId);
        User user = userRepository.findByChatId(chatId).orElseThrow(UserNotFoundException::new);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);
        switch (data){
            case "Начало":
                userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_DELIVERY);
                callBackAnswer = new SendMessage(String.valueOf(chatId),"Привет, "+user.getFirstName()+
                        "!"+ "\n"+"Нужно ли забрать груз?");
                callBackAnswer.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                        "Да", "Забрать", "Нет", "Привезет"));
                break;
            case "Забрать":
                if (botState==BotState.ASK_DELIVERY){
                    calculation.setDelivery(true);
                    callBackAnswer = boxQuantityHandler(chatId, calculation);}
                break;
            case "Привезет":
                if (botState==BotState.ASK_DELIVERY){
                    calculation.setDelivery(false);
                    callBackAnswer = boxQuantityHandler(chatId, calculation);
                }
                break;
            case "Отбраковка":
                if (botState==BotState.ASK_DEFECTIVE) {
                    calculation.setDefective(true);
                    callBackAnswer = smartDefectiveHandler(chatId, calculation);
                }
                break;
            case "Не отбраковывать":
                if (botState==BotState.ASK_DEFECTIVE) {
                    calculation.setDefective(false);
                    callBackAnswer = barcodeHandler(chatId, calculation);
                }
                break;
            case "Простая":
                if (botState==BotState.ASK_SMART_DEFECTIVE) {
                    calculation.setSimple(true);
                    callBackAnswer = barcodeHandler(chatId, calculation);
                }
                break;
            case "Сложная":
                if (botState==BotState.ASK_SMART_DEFECTIVE) {
                    calculation.setSimple(false);
                    callBackAnswer = barcodeHandler(chatId, calculation);
                }
                break;
            case "Штрихкод":
                if (botState==BotState.ASK_BARCODE) {
                    calculation.setBarcode(true);
                    callBackAnswer = packagingHandler(chatId, calculation);
                }
                break;
            case "Не штрихкод":
                if (botState==BotState.ASK_BARCODE) {
                    calculation.setBarcode(false);
                    callBackAnswer = packagingHandler(chatId, calculation);
                }
                break;
            case "Упаковать":
                if (botState==BotState.ASK_PACKAGING) {
                    calculation.setPackaging(true);
                    callBackAnswer = packagingPriceHandler(chatId, calculation);
                }
                break;
            case "10":
                if (botState==BotState.ASK_PACKAGING_PRICE) {
                    calculation.setPackagingPrice(10);
                    callBackAnswer = marketDeliveryHandler(chatId, calculation);}
                break;
            case "15":
                if (botState==BotState.ASK_PACKAGING_PRICE) {
                    calculation.setPackagingPrice(15);
                    callBackAnswer = marketDeliveryHandler(chatId, calculation);
                }
                break;
            case "20":
                if (botState==BotState.ASK_PACKAGING_PRICE) {
                    calculation.setPackagingPrice(20);
                    callBackAnswer = marketDeliveryHandler(chatId, calculation);
                }
                break;
            case "Не упаковывать":
                if (botState==BotState.ASK_PACKAGING) {
                    calculation.setPackaging(false);
                    callBackAnswer = marketDeliveryHandler(chatId, calculation);
                }
                break;
            case "Отгрузка":
                if (botState==BotState.ASK_MARKET_DELIVERY) {
                    calculation.setShipment(true);
                    callBackAnswer = prepareBoxesHandler(chatId, calculation);
                }
                break;
            case "Не отгружать":
                if (botState==BotState.ASK_MARKET_DELIVERY) {
                    calculation.setShipment(false);
                    calculation.setTotalPrice(countTotalPrice(calculation));
                    userDataCache.saveUserCalculation(chatId, calculation);
                    callBackAnswer = agreeHandler(chatId, calculation);}
                break;
            case "Подготовка коробов":
                if (botState==BotState.ASK_PREPARE_BOXES) {
                    calculation.setPrepareBoxes(true);
                    calculation.setTotalPrice(countTotalPrice(calculation));
                    userDataCache.saveUserCalculation(chatId, calculation);
                    callBackAnswer = agreeHandler(chatId, calculation);
                }
                break;
            case "Не готовить коробы":
                if (botState==BotState.ASK_PREPARE_BOXES) {
                    calculation.setPrepareBoxes(false);
                    calculation.setTotalPrice(countTotalPrice(calculation));
                    userDataCache.saveUserCalculation(chatId, calculation);
                    callBackAnswer = agreeHandler(chatId, calculation);
                }
                break;
            case "Ввод телефона":
                if (botState==BotState.ASK_PHONE) {
                    calculation.setUser(user);
                    calculationRepository.save(calculation);
                    callBackAnswer = new SendMessage(String.valueOf(chatId), "Введите номер телефона:");
                    userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_PHONE);
                }
                break;
            case "Отбой":
                userDataCache.setUsersCurrentBotState(chatId, BotState.FINISH);
                callBackAnswer = new SendMessage(String.valueOf(chatId), "Спасибо за обращение.");
                userDataCache.deleteUserCalculation(chatId);
                break;
        }

        return callBackAnswer;
    }

    private SendMessage packagingPriceHandler(long chatId, Calculation calculation) {
        SendMessage answer = new SendMessage(String.valueOf(chatId), "Выберите стоимости упаковки:");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var firstButton = InlineKeyboardButton.builder()
                .text("10")
                .callbackData("10")
                .build();

        var secondButton = InlineKeyboardButton.builder()
                .text("15")
                .callbackData("15")
                .build();
        var thirdButton = InlineKeyboardButton.builder()
                .text("20")
                .callbackData("20")
                .build();

        rowInline.add(firstButton);
        rowInline.add(secondButton);
        rowInline.add(thirdButton);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        answer.setReplyMarkup(markupInline);
        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_PACKAGING_PRICE);
        userDataCache.saveUserCalculation(chatId, calculation);
        return answer;
    }

    private SendMessage agreeHandler(long chatId, Calculation calculation) {
        SendMessage answer = new SendMessage(String.valueOf(chatId),
                "Итоговая цена: "+calculation.getTotalPrice()+" рублей. \n"+"Мне все подходит, свяжитесь со мной.");
        answer.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                "Да", "Ввод телефона", "Нет", "Отбой"));
        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_PHONE);
        return answer;
    }

    private SendMessage prepareBoxesHandler(long chatId,  Calculation calculation) {
        SendMessage answer = new SendMessage(String.valueOf(chatId), "Требуется ли дополнительная упаковка товара?");
        answer.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                "Да", "Подготовка коробов", "Нет", "Не готовить коробы"));
        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_PREPARE_BOXES);
        userDataCache.saveUserCalculation(chatId, calculation);
        return answer;
    }

    private SendMessage marketDeliveryHandler(long chatId, Calculation calculation) {
        SendMessage answer = new SendMessage(String.valueOf(chatId), "Требуется ли отгрузка на склад маркетплейса?");
        answer.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                "Да", "Отгрузка", "Нет", "Не отгружать"));
        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_MARKET_DELIVERY);
        userDataCache.saveUserCalculation(chatId, calculation);
        return answer;
    }

    private SendMessage packagingHandler(long chatId, Calculation calculation) {
        SendMessage answer = new SendMessage(String.valueOf(chatId), "Требуется ли дополнительная упаковка товара?");
        answer.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                "Да", "Упаковать", "Нет", "Не упаковывать"));
        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_PACKAGING);
        userDataCache.saveUserCalculation(chatId, calculation);
        return answer;
    }

    private SendMessage barcodeHandler(long chatId, Calculation calculation) {
        SendMessage answer = new SendMessage(String.valueOf(chatId),
                "Требуется ли штрихкодирование на заводской упаковке?");
        answer.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                "Да", "Штрихкод", "Нет", "Не штрихкод"));
        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_BARCODE);
        userDataCache.saveUserCalculation(chatId, calculation);
        return answer;
    }

    private SendMessage smartDefectiveHandler(long chatId, Calculation calculation) {
        SendMessage answer = new SendMessage(String.valueOf(chatId),"Отбраковка простая или сложная?");
        answer.setReplyMarkup(keyboardMarkupMaker.getInlineMessageButtons(
                "Простая", "Простая", "Сложная", "Сложная"));
        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_SMART_DEFECTIVE);
        userDataCache.saveUserCalculation(chatId, calculation);
        return answer;
    }

    private SendMessage boxQuantityHandler(long chatId, Calculation calculation) {
        SendMessage answer = new SendMessage(String.valueOf(chatId),"Введите объем поставки в эквиваленте 60х40х40:");
        userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_BOX_QUANTITY);
        userDataCache.saveUserCalculation(chatId, calculation);
        return answer;
    }

    private int countTotalPrice (Calculation calculation) {
        int price;
        int defectivePrice, brcodePrice, preparePrice;
        preparePrice = 0;
        brcodePrice = 0;
        defectivePrice = 1;
        if (calculation.isDefective()) {
            if (calculation.isSimple())
                defectivePrice = 2;
            else
                defectivePrice = 10;
        }
        if (calculation.isBarcode())
            brcodePrice = 10;
        if (calculation.isShipment()) {
            if (calculation.isPrepareBoxes())
                preparePrice = 300;
            else
                preparePrice = 149;
        }

        price = calculation.getBoxQuantity() * 50 + calculation.getItemQuantity() * defectivePrice +
                calculation.getItemQuantity() * brcodePrice + calculation.getPackagingPrice() * calculation.getItemQuantity()
                + preparePrice * calculation.getBoxQuantity();

        if (calculation.isDelivery())
            price += 1000;
        return price;
    }
}

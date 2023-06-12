package com.example.fulbot.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyboardMarkupMaker {
    String yesButtonText;
    String yesButtonData;
    String noButtonText;
    String noButtonData;

    public InlineKeyboardMarkup getInlineMessageButtons(String yesButtonText, String yesButtonData, String noButtonText,
                                                        String noButtonData){

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText(yesButtonText);
        yesButton.setCallbackData(yesButtonData);

        var noButton = new InlineKeyboardButton();
        noButton.setText(noButtonText);
        noButton.setCallbackData(noButtonData);

        rowInline.add(yesButton);
        rowInline.add(noButton);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}

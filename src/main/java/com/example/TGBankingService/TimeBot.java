package com.example.TGBankingService;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TimeBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleMessage(Message message) {
        long chatId = message.getChatId();
        String text = message.getText();

        if ("/start".equals(text) || "Варіанти: з 9 до 18 годин".equals(text)) {
            sendOptionsMessage(chatId);
        } else if ("Вимкнути повідомлення".equals(text)) {
            // Ваша логіка для вимкнення повідомлень
        }
    }

    private void sendOptionsMessage(long chatId) {
        InlineKeyboardMarkup keyboardMarkup = buildHourOptionsKeyboard();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Оберіть годину для надсилання повідомлень:");
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();

        if (data.matches("\\d+")) {
            int chosenHour = Integer.parseInt(data);
            // Логіка обробки обраної години
            sendMessage(chatId, "Години встановлено на " + chosenHour + ":00");
        } else if ("mute_notifications".equals(data)) {
            // Логіка для вимкнення повідомлень
            sendMessage(chatId, "Повідомлення вимкнено");
        }
    }

    private InlineKeyboardMarkup buildHourOptionsKeyboard() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int hour = 9; hour <= 18; hour++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(new InlineKeyboardButton().setText(String.format("%02d:00", hour)).callbackData(String.valueOf(hour)));
            rows.add(row);
        }
        List<InlineKeyboardButton> muteButtonRow = new ArrayList<>();
        muteButtonRow.add(new InlineKeyboardButton().setText("Вимкнути повідомлення").callbackData("mute_notifications"));
        rows.add(muteButtonRow);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "YourBotUsername";
    }

    @Override
    public String getBotToken() {
        return "YourBotToken";
    }
}

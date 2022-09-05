package com.DYShunyaev.telegram_bot.service;


import com.DYShunyaev.telegram_bot.config.BotConfig;
import com.DYShunyaev.telegram_bot.model.User;
import com.DYShunyaev.telegram_bot.model.UserRepository;
import com.DYShunyaev.telegram_bot.parser.Horoscope;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;
    static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities.\n\n" +
            "You can execute commands from  the main menu on the left or by tapping a command:\n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /mydata to see data stored yourself\n\n" +
            "Type /help to see this message again";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start","get a welcome message."));
        listofCommands.add(new BotCommand("/mydata","get your data stored."));
        listofCommands.add(new BotCommand("/deletedata","delete my data"));
        listofCommands.add(new BotCommand("help","info how to use this bot"));
        listofCommands.add(new BotCommand("/settings","set your preferences"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(),null));
        }
        catch (TelegramApiException e){
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":

                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                break;
                case "/help": sendMessage(chatId,HELP_TEXT);
                break;
                case "/mydata": sendMessage(chatId, selectDataUser(chatId));
                break;
                case "/deletedata": deleteUserDate(chatId, update.getMessage().getChat().getFirstName());
                break;
                case "horoscope":
                    sendHoroscope(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default: sendMessage(chatId, "Sorry, command was not recognized.");
            }
        }

        else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();

            switch (callBackData) {
                case "Овен" -> setHoroscope("Овен", chatId, messageId);
                case "Телец" -> setHoroscope("Телец", chatId, messageId);
                case "Близнецы" -> setHoroscope("Близнецы", chatId, messageId);
                case "Рак" -> setHoroscope("Рак", chatId, messageId);
                case "Лев" -> setHoroscope("Лев", chatId, messageId);
                case "Дева" -> setHoroscope("Дева", chatId, messageId);
                case "Весы" -> setHoroscope("Весы", chatId, messageId);
                case "Скорпион" -> setHoroscope("Скорпион", chatId, messageId);
                case "Стрелец" -> setHoroscope("Стрелец", chatId, messageId);
                case "Козерог" -> setHoroscope("Козерог", chatId, messageId);
                case "Водолей" -> setHoroscope("Водолей", chatId, messageId);
                case "Рыбы" -> setHoroscope("Рыбы", chatId, messageId);
            }
        }
    }

    private void setHoroscope(String zodiac, long chatId, long messageId) throws IOException {
        String text = Horoscope.getHoroscope(zodiac);
        Horoscope.clearSB();
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(text);
        message.setMessageId((int) messageId);
        try{
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
    private void sendHoroscope(long chatId, String name) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберете знак зодиака:");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        List<InlineKeyboardButton> row6 = new ArrayList<>();

        var buttonAries = new InlineKeyboardButton();

        buttonAries.setText("Овен");
        buttonAries.setCallbackData("Овен");

        var buttonTaurus = new InlineKeyboardButton();

        buttonTaurus.setText("Телец");
        buttonTaurus.setCallbackData("Телец");

        row1.add(buttonAries);
        row1.add(buttonTaurus);

        var buttonGemini = new InlineKeyboardButton();

        buttonGemini.setText("Близнецы");
        buttonGemini.setCallbackData("Близнецы");

        var buttonCancer = new InlineKeyboardButton();

        buttonCancer.setText("Рак");
        buttonCancer.setCallbackData("Рак");

        row2.add(buttonGemini);
        row2.add(buttonCancer);

        var buttonLeo = new InlineKeyboardButton();

        buttonLeo.setText("Лев");
        buttonLeo.setCallbackData("Лев");

        var buttonVirgo = new InlineKeyboardButton();

        buttonVirgo.setText("Дева");
        buttonVirgo.setCallbackData("Дева");

        row3.add(buttonLeo);
        row3.add(buttonVirgo);

        var buttonLibra = new InlineKeyboardButton();

        buttonLibra.setText("Весы");
        buttonLibra.setCallbackData("Весы");

        var buttonScorpio = new InlineKeyboardButton();

        buttonScorpio.setText("Скорпион");
        buttonScorpio.setCallbackData("Скорпион");

        row4.add(buttonLibra);
        row4.add(buttonScorpio);

        var buttonSagittarius = new InlineKeyboardButton();

        buttonSagittarius.setText("Стрелец");
        buttonSagittarius.setCallbackData("Стрелец");

        var buttonCapricorn = new InlineKeyboardButton();

        buttonCapricorn.setText("Козерог");
        buttonCapricorn.setCallbackData("Козерог");

        row5.add(buttonSagittarius);
        row5.add(buttonCapricorn);

        var buttonAquarius = new InlineKeyboardButton();

        buttonAquarius.setText("Водолей");
        buttonAquarius.setCallbackData("Водолей");

        var buttonPisces = new InlineKeyboardButton();

        buttonPisces.setText("Рыбы");
        buttonPisces.setCallbackData("Рыбы");

        row6.add(buttonAquarius);
        row6.add(buttonPisces);

        rowsInline.add(row1);
        rowsInline.add(row2);
        rowsInline.add(row3);
        rowsInline.add(row4);
        rowsInline.add(row5);
        rowsInline.add(row6);

        markupInLine.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInLine);

        try{
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

     }

    private void registerUser(Message msg) {

        if (userRepository.findById(msg.getChatId()).isEmpty()) {

            var chatId = msg.getChatId();
            var chat  = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("User save: " + user);
        }
    }

    private void deleteUserDate(long chatId, String name) {
        userRepository.deleteById(chatId);
        String answer = EmojiParser.parseToUnicode(name + ", everything is deleted." + " :smirk:");
        log.info("Delete data from " + name);
        sendMessage(chatId, answer);
    }

    private String selectDataUser(long chatId) {
        return userRepository.findById(chatId).toString();
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :relaxed:");
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("weather");
        row.add("horoscope");

        keyboardRows.add(row);

        row = new KeyboardRow();

        row.add("register");
        row.add("check my data");
        row.add("delete my data");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        try{
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}

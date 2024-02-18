package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.services.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//Is it Controller?
@Component
public class Bot {
    private final TelegramBot telegramBot;
    private final ApplicationConfig applicationConfig;
    private final CommandService commandService;

    @Autowired
    public Bot(ApplicationConfig applicationConfig, CommandService commandService) {
        this.applicationConfig = applicationConfig;
        this.commandService = commandService;
        telegramBot = new TelegramBot(applicationConfig.telegramToken());
        telegramBot.setUpdatesListener(updates -> {
            Integer lastId = UpdatesListener.CONFIRMED_UPDATES_NONE;
            for (Update update : updates) {
                if (!handleMessage(update)) {
                    return lastId;
                }
                lastId = update.updateId();
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private boolean handleMessage(Update update) {
        if (update.message() == null || update.message().chat() == null) {
            return true;
        }
        return commandService.processCommand(this, update);
    }

    public boolean writeToUser(Update update, String text) {
        SendMessage request = new SendMessage(update.message().chat().id(), text);
        SendResponse response = telegramBot.execute(request);
        return response.isOk();
    }
}
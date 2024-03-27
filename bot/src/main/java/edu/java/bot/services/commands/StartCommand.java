package edu.java.bot.services.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperApiException;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.clients.dto.ApiErrorResponse;
import edu.java.bot.data.UsersTracks;
import edu.java.bot.services.ICommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements ICommand {
    private ScrapperClient scrapperClient;
    private static final String START_MESSAGE = "Удачно использовать трэкер! Для спраки обращаться в /help\n";

    StartCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "Введите, чтобы начать";
    }

    @Override
    public String processCommand(Update update) {
        scrapperClient.postTgChat(update.message().chat().id());
        return START_MESSAGE;

        //TODO:: maybe, real registration??
    }
}

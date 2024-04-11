package edu.java.bot.services.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.data.UsersWaiting;
import edu.java.bot.services.ICommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand implements ICommand {
    private ScrapperClient scrapperClient;
    private static final String REQUEST = "Пожалуйста, введите URL, который надо трэкать:";
    private static final String URL_ADDED = "URL был добавлен";
    private UsersWaiting usersWaiting;

    @Autowired TrackCommand(ScrapperClient scrapperClient, UsersWaiting usersWaiting) {
        this.scrapperClient = scrapperClient;
        this.usersWaiting = usersWaiting;
    }

    @Override
    public String getName() {
        return "/track";
    }

    @Override
    public String getDescription() {
        return "Начать отслеживание ссылки";
    }

    @Override
    public String processCommand(Update update) {
        if (!usersWaiting.getWaiting(update.message().chat().id()).equals(getName())) {
            return requestURL(update);
        } else {
            return addURL(update);
        }
    }

    private String requestURL(Update update) {
        usersWaiting.setWaiting(update.message().chat().id(), getName());
        return REQUEST;
    }

    private String addURL(Update update) {
        usersWaiting.setWaiting(update.message().chat().id(), usersWaiting.getDefaultWaiting());
        if (scrapperClient.postLink(update.message().chat().id(), update.message().text()) != null) {
            return URL_ADDED;
        } else {
            return SCRAPPER_ERROR;
        }
    }
}

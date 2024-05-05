package edu.java.bot.services.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.data.UsersWaiting;
import edu.java.bot.services.ICommand;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand implements ICommand {
    private ScrapperClient scrapperClient;
    final private UsersWaiting usersWaiting;
    private static final String REQUEST = "Пожалуйста, введите URL, который вы хотите перестать трэкать:";
    private static final String URL_REMOVED = "URL был удалён";

    UntrackCommand(ScrapperClient scrapperClient, UsersWaiting usersWaiting) {
        this.scrapperClient = scrapperClient;
        this.usersWaiting = usersWaiting;
    }

    @Override
    public String getName() {
        return "/untrack";
    }

    @Override
    public String getDescription() {
        return "Прекратить отслеживание ссылки";
    }

    @Override
    public String processCommand(Update update) {
        if (!usersWaiting.getWaiting(update.message().chat().id()).equals(getName())) {
            return requestURL(update);
        } else {
            return removeURL(update);
        }
    }

    private String requestURL(Update update) {
        usersWaiting.setWaiting(update.message().chat().id(), getName());
        return REQUEST;
    }

    private String removeURL(Update update) {
        usersWaiting.setWaiting(update.message().chat().id(), usersWaiting.getDefaultWaiting());
        if (scrapperClient.deleteLink(update.message().chat().id(), update.message().text()) != null) {
            return URL_REMOVED;
        } else {
            return SCRAPPER_ERROR;
        }
    }
}

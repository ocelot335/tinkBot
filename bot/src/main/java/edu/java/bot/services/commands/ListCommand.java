package edu.java.bot.services.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.clients.dto.LinkResponse;
import edu.java.bot.clients.dto.ListLinkResponse;
import edu.java.bot.services.ICommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListCommand implements ICommand {
    private static final String NO_TRACKED = "В данный момент вы не подписаны не на одну ссылку";
    private static final String ANY_TRACKED = "В данный момент вы подписаны на следующие ссылки:\n";

    private ScrapperClient scrapperClient;

    @Autowired ListCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String getName() {
        return "/list";
    }

    @Override
    public String getDescription() {
        return "Показать список отслеживаемых ссылок";
    }

    @Override
    public String processCommand(Update update) {
        ListLinkResponse links = scrapperClient.getLinks(update.message().chat().id());
        if (links == null || links.getSize() == 0) {
            return NO_TRACKED;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ANY_TRACKED);
            for (LinkResponse link : links.getLinks()) {
                stringBuilder.append(link.getUrl());
                stringBuilder.append("\n");
            }
            stringBuilder.trimToSize();
            return stringBuilder.toString();
        }
    }
}

package edu.java.services;

import edu.java.clients.BotClient;
import edu.java.clients.dto.LinkUpdate;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HttpMessageTransporter implements IMessageTransporter {
    private final BotClient botClient;

    @Override
    public void send(LinkUpdate update) {
        botClient.postUpdates(update);
    }
}

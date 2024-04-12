package edu.java.bot.services;

import edu.java.bot.bot.Bot;
import edu.java.bot.controller.dto.LinkUpdate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class HandleLinkUpdateService {
    private final Bot bot;

    public void handle(LinkUpdate message) {
        bot.callUsers(message.getUrl(), message.getDescription(), message.getTgChatIds());
    }
}

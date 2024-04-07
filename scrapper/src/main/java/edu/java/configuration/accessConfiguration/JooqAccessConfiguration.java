package edu.java.configuration.accessConfiguration;

import edu.java.clients.BotClient;
import edu.java.clients.apiclients.IAPIClient;
import edu.java.domain.jooq.JooqChatsDAO;
import edu.java.domain.jooq.JooqLinksDAO;
import edu.java.domain.jooq.JooqSubscribesDAO;
import edu.java.services.interfaces.ILinkUpdateService;
import edu.java.services.interfaces.ISubscribeService;
import edu.java.services.interfaces.ITgChatService;
import edu.java.services.jooq.JooqLinkUpdateService;
import edu.java.services.jooq.JooqSubscribeService;
import edu.java.services.jooq.JooqTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {
    @Bean
    public ILinkUpdateService linkUpdateService(
        JooqLinksDAO linkRepository,
        BotClient botClient,
        IAPIClient[] apiClients,
        JooqSubscribesDAO subscribesRepository
    ) {
        return new JooqLinkUpdateService(linkRepository, botClient, apiClients, subscribesRepository);
    }

    @Bean
    public ISubscribeService subscribeService(
        JooqChatsDAO chatRepository,
        JooqLinksDAO linkRepository,
        JooqSubscribesDAO subscribesRepository,
        IAPIClient[] clients
    ) {
        return new JooqSubscribeService(chatRepository, linkRepository, subscribesRepository, clients);
    }

    @Bean
    public ITgChatService tgChatService(
        JooqChatsDAO chatRepository
    ) {
        return new JooqTgChatService(chatRepository);
    }
}

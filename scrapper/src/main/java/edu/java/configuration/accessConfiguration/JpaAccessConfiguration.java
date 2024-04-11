package edu.java.configuration.accessConfiguration;

import edu.java.clients.BotClient;
import edu.java.clients.apiclients.IAPIClient;
import edu.java.domain.jpa.JpaChatsDAO;
import edu.java.domain.jpa.JpaLinksDAO;
import edu.java.services.interfaces.ILinkUpdateService;
import edu.java.services.interfaces.ISubscribeService;
import edu.java.services.interfaces.ITgChatService;
import edu.java.services.jpa.JpaLinkUpdateService;
import edu.java.services.jpa.JpaSubscribeService;
import edu.java.services.jpa.JpaTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    public ILinkUpdateService linkUpdateService(
        JpaLinksDAO linkRepository,
        BotClient botClient,
        IAPIClient[] apiClients
    ) {
        return new JpaLinkUpdateService(linkRepository, botClient, apiClients);
    }

    @Bean
    public ISubscribeService subscribeService(
        JpaChatsDAO chatRepository,
        JpaLinksDAO linkRepository,
        IAPIClient[] clients
    ) {
        return new JpaSubscribeService(chatRepository, linkRepository, clients);
    }

    @Bean
    public ITgChatService tgChatService(
        JpaChatsDAO chatRepository
    ) {
        return new JpaTgChatService(chatRepository);
    }
}

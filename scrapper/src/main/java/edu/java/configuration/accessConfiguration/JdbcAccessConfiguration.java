package edu.java.configuration.accessConfiguration;

import edu.java.clients.apiclients.IAPIClient;
import edu.java.domain.jdbc.JdbcChatsDAO;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jdbc.JdbcSubscribesDAO;
import edu.java.services.IMessageTransporter;
import edu.java.services.interfaces.ILinkUpdateService;
import edu.java.services.interfaces.ISubscribeService;
import edu.java.services.interfaces.ITgChatService;
import edu.java.services.jdbc.JdbcLinkUpdateService;
import edu.java.services.jdbc.JdbcSubscribeService;
import edu.java.services.jdbc.JdbcTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public ILinkUpdateService linkUpdateService(
        JdbcLinksDAO linkRepository,
        IMessageTransporter messageTransporter,
        IAPIClient[] apiClients,
        JdbcSubscribesDAO subscribesRepository
    ) {
        return new JdbcLinkUpdateService(linkRepository, messageTransporter, apiClients, subscribesRepository);
    }

    @Bean
    public ISubscribeService subscribeService(
        JdbcChatsDAO chatRepository,
        JdbcLinksDAO linkRepository,
        JdbcSubscribesDAO subscribesRepository,
        IAPIClient[] clients
    ) {
        return new JdbcSubscribeService(chatRepository, linkRepository, subscribesRepository, clients);
    }

    @Bean
    public ITgChatService tgChatService(
        JdbcChatsDAO chatRepository
    ) {
        return new JdbcTgChatService(chatRepository);
    }
}

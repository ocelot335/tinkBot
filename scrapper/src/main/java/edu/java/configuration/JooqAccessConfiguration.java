package edu.java.configuration;

import edu.java.clients.BotClient;
import edu.java.clients.apiclients.IAPIClient;
import edu.java.domain.jdbc.JdbcChatsDAO;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jdbc.JdbcSubscribesDAO;
import edu.java.domain.jooq.JooqChatsDAO;
import edu.java.domain.jooq.JooqLinksDAO;
import edu.java.domain.jooq.JooqSubscribesDAO;
import edu.java.services.interfaces.ILinkUpdateService;
import edu.java.services.interfaces.ISubscribeService;
import edu.java.services.interfaces.ITgChatService;
import edu.java.services.jdbc.JdbcLinkUpdateService;
import edu.java.services.jdbc.JdbcSubscribeService;
import edu.java.services.jdbc.JdbcTgChatService;
import edu.java.services.jooq.JooqLinkUpdateService;
import edu.java.services.jooq.JooqSubscribeService;
import edu.java.services.jooq.JooqTgChatService;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import java.sql.Connection;

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

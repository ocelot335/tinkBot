package edu.java.configuration.messageTransporterConfiguration;

import edu.java.clients.BotClient;
import edu.java.services.HttpMessageTransporter;
import edu.java.services.IMessageTransporter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "message-transporter-type", havingValue = "http")
public class HttpMessageTransporterConfiguration {
    @Bean
    public IMessageTransporter messageTransporter(BotClient botClient) {
        return new HttpMessageTransporter(botClient);
    }
}

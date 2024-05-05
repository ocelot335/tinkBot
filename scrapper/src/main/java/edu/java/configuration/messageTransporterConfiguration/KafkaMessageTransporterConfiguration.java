package edu.java.configuration.messageTransporterConfiguration;

import edu.java.clients.dto.LinkUpdate;
import edu.java.configuration.ApplicationConfig;
import edu.java.services.IMessageTransporter;
import edu.java.services.ScrapperQueueProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "message-transporter-type", havingValue = "kafka")
public class KafkaMessageTransporterConfiguration {
    @Bean
    public IMessageTransporter messageTransporter(
        KafkaTemplate<Long, LinkUpdate> template,
        ApplicationConfig.KafkaTopics kafkaTopics
    ) {
        return new ScrapperQueueProducer(template, kafkaTopics);
    }
}

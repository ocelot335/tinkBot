package edu.java.services;

import edu.java.clients.dto.LinkUpdate;
import edu.java.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class ScrapperQueueProducer implements IMessageTransporter {
    private final KafkaTemplate<Long, LinkUpdate> template;
    private final ApplicationConfig.KafkaTopics kafkaTopics;

    public void send(LinkUpdate update) {
        try {
            template.send(kafkaTopics.messageTopic().name(), update.getId(), update);
            log.info("Сообщение отправлено в топик: " + kafkaTopics.messageTopic().name());
        } catch (Exception ex) {
            log.error("Error occurred during sending to Kafka", ex);
        }
    }
}

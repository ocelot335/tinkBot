package edu.java.bot.services;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.controller.dto.LinkUpdate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class KafkaMessageDlqService {
    private final KafkaTemplate<Long, LinkUpdate> template;
    private final ApplicationConfig.KafkaTopics kafkaTopics;

    public void sendDlq(LinkUpdate badMessage) {
        try {
            template.send(kafkaTopics.messagesDlqTopic().name(), badMessage.getId(), badMessage);
            log.info(
                "Необработанное сообщение отправлено в соответсвующий топик: " + kafkaTopics.messagesDlqTopic().name());
        } catch (Exception ex) {
            log.error("Необработанное сообщение не получилось добавить в Kafka", ex);
        }
    }
}

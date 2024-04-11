package edu.java.bot.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {
    ApplicationConfig.KafkaTopics kafkaTopics;

    public KafkaConfiguration(ApplicationConfig.KafkaTopics kafkaTopics) {
        this.kafkaTopics = kafkaTopics;
    }

    @Bean
    public NewTopic messageTopic() {
        return TopicBuilder.name(kafkaTopics.messageTopic().name())
            .partitions(kafkaTopics.messageTopic().partitions())
            .replicas(kafkaTopics.messageTopic().replicas())
            .build();
    }
}

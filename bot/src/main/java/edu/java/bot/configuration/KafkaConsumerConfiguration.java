package edu.java.bot.configuration;

import edu.java.bot.controller.dto.LinkUpdate;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {
    @Bean
    public ConsumerFactory<Long, LinkUpdate>
    taskInfoConsumerFactory(ApplicationConfig.KafkaConsumerConfig kafkaConfig) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.bootstrapServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.groupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.autoOffsetReset());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConfig.maxPollIntervalMs());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConfig.enableAutoCommit());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdate.class);
        props.put(JsonDeserializer.KEY_DEFAULT_TYPE, Long.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, LinkUpdate> messagesContainerFactory(
        ConsumerFactory<Long, LinkUpdate> consumerFactory,
        ApplicationConfig.KafkaConsumerConfig kafkaConfig
    ) {
        ConcurrentKafkaListenerContainerFactory<Long, LinkUpdate> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(kafkaConfig.concurrency());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    //Где вообще правильно создавать топики? Здесь? И здесь и у продюсера? В compose файле? В сторонних сервисах?
    @Bean
    public NewTopic topic1(ApplicationConfig.KafkaTopics kafkaTopics) {
        return TopicBuilder.name(kafkaTopics.messagesTopic().name())
            .partitions(kafkaTopics.messagesTopic().partitions())
            .replicas(kafkaTopics.messagesTopic().replicas())
            .build();
    }
}

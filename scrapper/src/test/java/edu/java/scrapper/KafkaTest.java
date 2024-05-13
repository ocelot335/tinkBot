package edu.java.scrapper;

import edu.java.clients.dto.LinkUpdate;
import edu.java.configuration.ApplicationConfig;
import edu.java.services.IMessageTransporter;
import edu.java.services.ScrapperQueueProducer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@Testcontainers
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class KafkaTest {

    @Autowired
    private IMessageTransporter kafkaProducer;

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private KafkaTemplate<Long, LinkUpdate> kafkaTemplate;

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.message-transporter-type",()->"kafka");
    }

    @Test
    public void kafkaTest() {
        String topic = config.kafkaTopics().messageTopic().name();
        List<Long> usersToNotify = new ArrayList<>();
        usersToNotify.add(24L);
        usersToNotify.add(240L);
        LinkUpdate linkUpdate = new LinkUpdate(42L, "test", "descr", usersToNotify);
        kafkaProducer.send(linkUpdate);
        LinkUpdate receivedUpdate = kafkaTemplate.receive(topic, 0, 0).value();
        assertEquals(linkUpdate.getId(), receivedUpdate.getId());
        assertEquals(linkUpdate.getUrl(), receivedUpdate.getUrl());
        assertEquals(linkUpdate.getDescription(), receivedUpdate.getDescription());
        assertEquals(linkUpdate.getTgChatIds(), receivedUpdate.getTgChatIds());
    }
}

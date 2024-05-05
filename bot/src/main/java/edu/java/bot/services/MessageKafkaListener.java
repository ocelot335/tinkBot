package edu.java.bot.services;

import edu.java.bot.controller.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageKafkaListener {
    private final HandleLinkUpdateService handler;
    private final KafkaMessageDlqService dlqService;

    @KafkaListener(topics = "#{@kafkaTopics.messagesTopic.name}", containerFactory = "messagesContainerFactory")
    public void handleMessage(@Payload LinkUpdate message, Acknowledgment acknowledgment) {
        try {
            handler.handle(message);
            log.info(
                "Получено новое сообщение от Kafka: обновление на " + message.getUrl() + " для "
                    + message.getTgChatIds());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Произошла следующая ошибка: " + e.getMessage());
            dlqService.sendDlq(message);
        }
    }
}

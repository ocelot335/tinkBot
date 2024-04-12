    package edu.java.bot.services;

    import edu.java.bot.bot.Bot;
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
        private final Bot bot;

        @KafkaListener(topics = "messages", containerFactory = "messagesContainerFactory")
        public void handleMessage(@Payload LinkUpdate message, Acknowledgment acknowledgment) {
            bot.callUsers(message.getUrl(), message.getDescription(), message.getTgChatIds());
            log.info("Получено новое сообщение от Kafka");
            acknowledgment.acknowledge();
        }
    }

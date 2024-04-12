package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    @NotNull BasicURLs basicURLs,
    @NotNull RetryClients retryClients,
    @NotNull @Bean RateLimits rateLimits,

    @NotNull @Bean ApplicationConfig.KafkaConsumerConfig kafkaConsumerConfig,
    @NotNull @Bean ApplicationConfig.KafkaProducerConfig kafkaProducerConfig,

    @NotNull @Bean KafkaTopics kafkaTopics
) {
    public record BasicURLs(String scrapperBasicURL) {
    }

    public record RetryClients(RetryClient scrapperRetry) {
    }

    public record RetryClient(RetryMode retryMode, List<String> retryCodes, Duration duration, int maxAttempts) {
    }

    public enum RetryMode {
        CONSTANT, LINEAR, EXPONENTIAL
    }

    public record RateLimits(Long capacity, Long tokens, Duration period) {
    }

    public record KafkaConsumerConfig(String bootstrapServer, String groupId, String autoOffsetReset,
                                      Integer maxPollIntervalMs,
                                      Boolean enableAutoCommit, Integer concurrency) {
    }

    public record KafkaProducerConfig(String bootstrapServer, String clientId, String acksMode,
                                      Duration deliveryTimeout, Long lingerMs, Integer batchSize) {
    }

    public record KafkaTopics(KafkaTopic messagesTopic, KafkaTopic messagesDlqTopic) {
    }

    public record KafkaTopic(String name, Integer partitions, Integer replicas) {
    }
}

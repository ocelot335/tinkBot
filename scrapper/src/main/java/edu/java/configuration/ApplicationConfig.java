package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@EnableScheduling
public record ApplicationConfig(
    @NotNull MessageTransporterType messageTransporterType,

    @NotNull AccessType databaseAccessType,
    @NotNull @Bean Scheduler scheduler,
    @NotNull BasicURLs basicURLs,
    @NotNull RetryClients retryClients,
    @NotNull @Bean RateLimits rateLimits,
    @NotNull @Bean KafkaProducerConfig kafkaProducerConfig,
    @NotNull @Bean KafkaTopics kafkaTopics
) {

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record BasicURLs(String gitHubBasicURL, String stackOverflowBasicURL, String botBasicURL) {
    }

    public enum AccessType {
        JDBC, JPA, JOOQ
    }

    public record RetryClients(RetryClient botRetry, RetryClient gitHubRetry, RetryClient stackOverflowRetry) {
    }

    public record RetryClient(RetryMode retryMode, List<String> retryCodes, Duration duration, int maxAttempts) {
    }

    public enum RetryMode {
        CONSTANT, LINEAR, EXPONENTIAL
    }

    public record RateLimits(Long capacity, Long tokens, Duration period) {
    }

    public record KafkaProducerConfig(String bootstrapServer, String clientId, String acksMode,
                                      Duration deliveryTimeout, Long lingerMs, Integer batchSize) {
    }

    public record KafkaTopics(KafkaTopic messageTopic) {
    }

    public record KafkaTopic(String name, Integer partitions, Integer replicas) {
    }

    public enum MessageTransporterType {
        HTTP, KAFKA
    }
}

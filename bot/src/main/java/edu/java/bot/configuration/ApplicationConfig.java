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
    @NotNull @Bean RateLimits rateLimits
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
}

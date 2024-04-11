package edu.java.bot.configuration;

import edu.java.bot.clients.ScrapperApiException;
import edu.java.bot.clients.ScrapperClient;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Configuration
public class ClientConfiguration {
    private final ApplicationConfig applicationConfig;

    @Autowired
    public ClientConfiguration(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public ScrapperClient scrapperClient() {
        ApplicationConfig.RetryClient retryConfig = applicationConfig.retryClients().scrapperRetry();
        RetryConfig config = RetryConfig.custom()
            .intervalFunction(getBackoff(retryConfig.retryMode(), retryConfig.duration()))
            .maxAttempts(retryConfig.maxAttempts())
            .retryOnException(e -> {
                if (e instanceof ScrapperApiException
                    && retryConfig.retryCodes().contains(((ScrapperApiException) e).getCode())) {
                    return true;
                } else if (e instanceof WebClientException) {
                    return true;
                }
                return false;
            })
            .build();

        return new ScrapperClient(WebClient.builder()
            .baseUrl(applicationConfig.basicURLs().scrapperBasicURL())
            .build(), RetryRegistry.of(config).retry("scrapperClient"));
    }

    private IntervalFunction getBackoff(ApplicationConfig.RetryMode mode, Duration duration) {
        return switch (mode) {
            case ApplicationConfig.RetryMode.CONSTANT -> IntervalFunction.of(duration);
            case LINEAR -> IntervalFunction.of(duration, (x) -> x + duration.toMillis());
            case EXPONENTIAL -> IntervalFunction.ofExponentialBackoff(duration);
        };
    }
}

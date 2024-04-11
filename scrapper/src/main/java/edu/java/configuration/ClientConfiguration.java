package edu.java.configuration;

import edu.java.clients.BotApiException;
import edu.java.clients.BotClient;
import edu.java.clients.apiclients.APIException;
import edu.java.clients.apiclients.GitHubClient;
import edu.java.clients.apiclients.StackOverflowClient;
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
    private ApplicationConfig applicationConfig;

    @Autowired
    public ClientConfiguration(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public GitHubClient gitHubClient() {
        ApplicationConfig.RetryClient retryConfig = applicationConfig.retryClients().gitHubRetry();
        RetryConfig config = RetryConfig.custom()
            .intervalFunction(getBackoff(retryConfig.retryMode(), retryConfig.duration()))
            .maxAttempts(retryConfig.maxAttempts())
            .retryOnException(e -> {
                if (e instanceof APIException
                    && retryConfig.retryCodes().contains(((APIException) e).getCode())) {
                    return true;
                } else if (e instanceof WebClientException) {
                    return true;
                }
                return false;
            })
            .build();
        return new GitHubClient(WebClient.builder()
            .baseUrl(applicationConfig.basicURLs().gitHubBasicURL())
            .build(), RetryRegistry.of(config).retry("gitHubClient"));
    }

    @Bean
    public StackOverflowClient stackOverFlowClient() {
        ApplicationConfig.RetryClient retryConfig = applicationConfig.retryClients().stackOverflowRetry();
        RetryConfig config = RetryConfig.custom()
            .intervalFunction(getBackoff(retryConfig.retryMode(), retryConfig.duration()))
            .maxAttempts(retryConfig.maxAttempts())
            .retryOnException(e -> {
                if (e instanceof APIException
                    && retryConfig.retryCodes().contains(((APIException) e).getCode())) {
                    return true;
                } else if (e instanceof WebClientException) {
                    return true;
                }
                return false;
            })
            .build();
        return new StackOverflowClient(WebClient.builder()
            .baseUrl(applicationConfig.basicURLs().stackOverflowBasicURL())
            .build(), RetryRegistry.of(config).retry("stackOverflowClient"));
    }

    @Bean
    public BotClient botClient() {
        ApplicationConfig.RetryClient retryConfig = applicationConfig.retryClients().botRetry();
        RetryConfig config = RetryConfig.custom()
            .intervalFunction(getBackoff(retryConfig.retryMode(), retryConfig.duration()))
            .maxAttempts(retryConfig.maxAttempts())
            .retryOnException(e -> {
                if (e instanceof BotApiException
                    && retryConfig.retryCodes().contains(((BotApiException) e).getCode())) {
                    return true;
                } else if (e instanceof WebClientException) {
                    return true;
                }
                return false;
            })
            .build();
        return new BotClient(WebClient.builder()
            .baseUrl(applicationConfig.basicURLs().botBasicURL())
            .build(), RetryRegistry.of(config).retry("botClient"));
    }

    private IntervalFunction getBackoff(ApplicationConfig.RetryMode mode, Duration duration) {
        return switch (mode) {
            case ApplicationConfig.RetryMode.CONSTANT -> IntervalFunction.of(duration);
            case LINEAR -> IntervalFunction.of(duration, (x) -> x + duration.toMillis());
            case EXPONENTIAL -> IntervalFunction.ofExponentialBackoff(duration);
        };
    }
}

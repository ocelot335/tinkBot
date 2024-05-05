package edu.java.clients;

import edu.java.clients.dto.ApiErrorResponse;
import edu.java.clients.dto.LinkUpdate;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
public class BotClient {
    private final WebClient webClient;
    private final Retry retry;

    public void postUpdates(LinkUpdate linkUpdate) {
        try {
            Mono<Void> request = webClient.post()
                .uri("/updates")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(linkUpdate))
                .retrieve()
                .onStatus(
                    HttpStatus.BAD_REQUEST::equals,
                    response -> response.bodyToMono(ApiErrorResponse.class).map(BotApiException::new)
                )
                .bodyToMono(Void.class);
            retry.executeSupplier(request::block);
        } catch (WebClientRequestException e) {
            log.error(e.toString());
        }
    }
}

package edu.java.clients;

import edu.java.clients.dto.ApiErrorResponse;
import edu.java.clients.dto.LinkUpdate;
import io.github.resilience4j.retry.Retry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Slf4j
public class BotClient {
    private final WebClient webClient;
    private final Retry retry;

    @Autowired
    public BotClient(WebClient webClient, Retry retry) {
        this.webClient = webClient;
        this.retry = retry;
    }

    public void postUpdates(Long id, String url, String description, List<Long> tgChatIds) {
        LinkUpdate linkUpdate = new LinkUpdate(id, url, description, tgChatIds);
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

package edu.java.bot.clients;

import edu.java.bot.clients.dto.AddLinkRequest;
import edu.java.bot.clients.dto.ApiErrorResponse;
import edu.java.bot.clients.dto.LinkResponse;
import edu.java.bot.clients.dto.ListLinkResponse;
import edu.java.bot.clients.dto.RemoveLinkRequest;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.function.Function;

@Slf4j
public class ScrapperClient {
    private final WebClient webClient;
    private final Retry retry;
    private static final String TG_CHAT_PATH = "/tg-chat/{id}";
    private static final String LINKS_PATH = "/links";
    private static final String LINKS_HEADER_TG_CHAT_ID = "Tg-Chat-Id";

    public ScrapperClient(WebClient webClient, Retry retry) {
        this.webClient = webClient;
        this.retry = retry;
    }

    public void postTgChat(Long id) {
        try {
            Mono<Void> request = webClient.post()
                .uri(TG_CHAT_PATH, id)
                .retrieve()
                .onStatus(
                    HttpStatus.BAD_REQUEST::equals,
                    response -> response.bodyToMono(ApiErrorResponse.class).map(ScrapperApiException::new)
                )
                .bodyToMono(Void.class);
            retry.executeSupplier(request::block);
        } catch (WebClientRequestException e) {
            log.error(e.toString());
        }
    }

    public void deleteTgChat(Long id) {
        try {
            Mono<Void> request =
                webClient.delete()
                    .uri(TG_CHAT_PATH, id)
                    .retrieve()
                    .onStatus(
                        status -> HttpStatus.BAD_REQUEST.equals(status) || HttpStatus.NOT_FOUND.equals(status),
                        response -> response.bodyToMono(ApiErrorResponse.class).map(ScrapperApiException::new)
                    )
                    .bodyToMono(Void.class);
            retry.executeSupplier(request::block);
        } catch (WebClientRequestException e) {
            log.error(e.toString());
        }
    }

    public ListLinkResponse getLinks(Long chatId) {
        try {
            Mono<ListLinkResponse> request = webClient.get()
                .uri(LINKS_PATH)
                .header(LINKS_HEADER_TG_CHAT_ID, chatId.toString())
                .retrieve()
                .onStatus(
                    status -> HttpStatus.BAD_REQUEST.equals(status) || HttpStatus.NOT_FOUND.equals(status),
                    response -> response.bodyToMono(ApiErrorResponse.class).map(ScrapperApiException::new)
                )
                .bodyToMono(ListLinkResponse.class);
            return retry.executeSupplier(request::block);
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    public LinkResponse postLink(Long chatId, String url) {
        try {
            Mono<LinkResponse> request = webClient.post()
                .uri(LINKS_PATH)
                .header(LINKS_HEADER_TG_CHAT_ID, chatId.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(new AddLinkRequest(url)))
                .retrieve()
                .onStatus(
                    status -> HttpStatus.BAD_REQUEST.equals(status) || HttpStatus.NOT_FOUND.equals(status),
                    response -> response.bodyToMono(ApiErrorResponse.class).map(ScrapperApiException::new)
                )
                .bodyToMono(LinkResponse.class);
            return retry.executeSupplier(request::block);
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }

    public LinkResponse deleteLink(Long chatId, String url) {
        try {
            Mono<LinkResponse> request = webClient.method(HttpMethod.DELETE)
                .uri(LINKS_PATH)
                .header(LINKS_HEADER_TG_CHAT_ID, chatId.toString())
                .body(BodyInserters.fromValue(new RemoveLinkRequest(url)))
                .retrieve()
                .onStatus(
                    status -> HttpStatus.BAD_REQUEST.equals(status) || HttpStatus.NOT_FOUND.equals(status),
                    response -> response.bodyToMono(ApiErrorResponse.class).map(ScrapperApiException::new)
                )
                .bodyToMono(LinkResponse.class);
            return retry.executeSupplier(request::block);
        } catch (WebClientRequestException e) {
            log.error(e.toString());
            return null;
        }
    }
}

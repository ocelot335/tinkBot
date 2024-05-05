package edu.java.clients.apiclients;

import edu.java.clients.responses.IAPIResponse;
import edu.java.clients.responses.StackOverflowResponse;
import edu.java.domain.dto.LinkDTO;
import io.github.resilience4j.retry.Retry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Slf4j
public class StackOverflowClient implements IAPIClient {
    private final WebClient webClient;
    private final Retry retry;

    @Autowired
    public StackOverflowClient(WebClient webClient, Retry retry) {
        this.webClient = webClient;
        this.retry = retry;
    }

    public StackOverflowResponse fetchQuestion(String questionId) {
        try {

            Mono<StackOverflowResponse> request = webClient.get()
                .uri("/questions/{questionId}?site=stackoverflow", questionId)
                .retrieve()
                .onStatus(
                    HttpStatusCode::isError,
                    clientResponse -> Mono.error(new APIException(clientResponse.statusCode()))
                )
                .bodyToMono(StackOverflowResponse.class);
            return retry.executeSupplier(request::block);
        } catch (WebClientRequestException | APIException e) {
            log.error(e.toString());
            return null;
        }
    }

    @Override
    public IAPIResponse getResponse(LinkDTO link) {
        return fetchQuestion(getQuestionId(link.getUrl()));
    }

    @Override
    public boolean isCorrectURL(String url) {
        Pattern questionPattern = Pattern.compile("^https?://stackoverflow\\.com/questions/(\\d+)/");
        Matcher questionMatcher = questionPattern.matcher(url);
        return questionMatcher.find();
    }

    private String getQuestionId(String url) {
        Pattern pattern = Pattern.compile("/questions/(\\d+)/");
        Matcher matcher = pattern.matcher(url);
        matcher.find();
        return matcher.group(1);
    }
}


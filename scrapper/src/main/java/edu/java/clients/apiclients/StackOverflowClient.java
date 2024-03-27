package edu.java.clients.apiclients;

import edu.java.clients.responses.IAPIResponse;
import edu.java.clients.responses.StackOverflowResponse;
import edu.java.domain.jdbc.dto.LinkDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackOverflowClient implements IAPIClient {
    private final WebClient webClient;

    @Autowired
    public StackOverflowClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public StackOverflowResponse fetchQuestion(String questionId) {
        return webClient.get()
            .uri("/questions/{questionId}?site=stackoverflow", questionId)
            .retrieve()
            .bodyToMono(StackOverflowResponse.class).block();
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


package edu.java.clients.apiclients;

import edu.java.clients.responses.IAPIResponse;
import edu.java.clients.responses.StackOverflowResponse;
import edu.java.domain.jdbc.dto.LinkDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class StackOverflowClient implements IAPIClient{
    private final WebClient webClient;

    @Autowired
    public StackOverflowClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<StackOverflowResponse> fetchQuestion(int questionId) {
        return webClient.get()
            .uri("/questions/{questionId}?site=stackoverflow", questionId)
            .retrieve()
            .bodyToMono(StackOverflowResponse.class);
    }

    @Override
    public IAPIResponse getResponse(LinkDTO link) {
        return null;
    }

    @Override
    public String getClientName() {
        return "stackoverflow.com";
    }
}


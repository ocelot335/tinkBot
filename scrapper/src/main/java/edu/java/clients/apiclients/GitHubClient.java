package edu.java.clients.apiclients;

import edu.java.clients.responses.GitHubResponse;
import edu.java.clients.responses.IAPIResponse;
import edu.java.domain.jdbc.dto.LinkDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class GitHubClient implements IAPIClient {

    private final WebClient webClient;

    @Autowired
    public GitHubClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<GitHubResponse> fetchRepository(String owner, String repo) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .retrieve()
            .bodyToMono(GitHubResponse.class);
    }

    @Override
    public IAPIResponse getResponse(LinkDTO link) {
        return null;
    }

    @Override
    public String getClientName() {
        return "github.com";
    }
}

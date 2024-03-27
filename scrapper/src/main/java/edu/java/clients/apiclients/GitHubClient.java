package edu.java.clients.apiclients;

import edu.java.clients.responses.GitHubResponse;
import edu.java.clients.responses.IAPIResponse;
import edu.java.domain.jdbc.dto.LinkDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubClient implements IAPIClient {

    private final WebClient webClient;

    @Autowired
    public GitHubClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public GitHubResponse fetchRepository(String owner, String repo) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .retrieve()
            .bodyToMono(GitHubResponse.class).block();
    }

    @Override
    public IAPIResponse getResponse(LinkDTO link) {
        Pattern gitHubPattern = Pattern.compile("^https?://github\\.com/([^/]+)/([^/]+)");
        Matcher gitHubMatcher = gitHubPattern.matcher(link.getUrl());
        gitHubMatcher.find();
        String owner = gitHubMatcher.group(1);
        String repo = gitHubMatcher.group(2);
        return fetchRepository(owner, repo);
    }

    @Override
    public boolean isCorrectURL(String url) {
        Pattern gitHubPattern = Pattern.compile("^https?://github\\.com/([^/]+)/([^/]+)");
        Matcher gitHubMatcher = gitHubPattern.matcher(url);
        return gitHubMatcher.find();
    }
}

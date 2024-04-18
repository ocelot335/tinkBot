package edu.java.clients.apiclients;

import edu.java.clients.responses.IAPIResponse;
import edu.java.clients.responses.github.GitHubEventsResponse;
import edu.java.clients.responses.github.GitHubRepositoryResponse;
import edu.java.domain.jdbc.dto.LinkDTO;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClient implements IAPIClient {

    private final WebClient webClient;
    private static final String GIT_HUB_REG_EXP = "^https?://github\\.com/([^/]+)/([^/]+)";

    @Autowired
    public GitHubClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public GitHubRepositoryResponse fetchRepository(String owner, String repo) {
        return webClient.get()
            .uri("/repos/{owner}/{repo}", owner, repo)
            .retrieve()
            .bodyToMono(GitHubRepositoryResponse.class).block();
    }

    @Override
    public IAPIResponse getResponse(LinkDTO link) {
        String owner = getOwner(link);
        String repo = getRepo(link);
        return fetchRepository(owner, repo);
    }

    public String getDescription(LinkDTO link, OffsetDateTime toDate) {
        String owner = getOwner(link);
        String repo = getRepo(link);
        List<GitHubEventsResponse.Event> events = webClient.get()
            .uri("/repos/{owner}/{repo}/events", owner, repo)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<GitHubEventsResponse.Event>>() {
            }).block();
        return getDescriptionFromEvents(events, toDate);
    }

    private String getDescriptionFromEvents(List<GitHubEventsResponse.Event> events, OffsetDateTime toDate) {
        StringBuilder sb = new StringBuilder();
        for (GitHubEventsResponse.Event event : events) {
            sb.append("--");
            sb.append(event.toString());
            sb.append("\n");
            if (event.getCreatedAt().isBefore(toDate)) {
                break;
            }
        }
        return sb.toString();

    }

    private String getOwner(LinkDTO link) {
        Pattern gitHubPattern = Pattern.compile(GIT_HUB_REG_EXP);
        Matcher gitHubMatcher = gitHubPattern.matcher(link.getUrl());
        gitHubMatcher.find();
        return gitHubMatcher.group(1);
    }

    private String getRepo(LinkDTO link) {
        Pattern gitHubPattern = Pattern.compile(GIT_HUB_REG_EXP);
        Matcher gitHubMatcher = gitHubPattern.matcher(link.getUrl());
        gitHubMatcher.find();
        return gitHubMatcher.group(2);
    }

    @Override
    public boolean isCorrectURL(String url) {
        Pattern gitHubPattern = Pattern.compile(GIT_HUB_REG_EXP);
        Matcher gitHubMatcher = gitHubPattern.matcher(url);
        return gitHubMatcher.find();
    }
}

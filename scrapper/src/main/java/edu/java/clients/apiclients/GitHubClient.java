package edu.java.clients.apiclients;

import edu.java.clients.responses.IAPIResponse;
import edu.java.clients.responses.github.GitHubEventsResponse;
import edu.java.domain.dto.LinkDTO;
import io.github.resilience4j.retry.Retry;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Slf4j
public class GitHubClient implements IAPIClient {

    private final WebClient webClient;
    private final Retry retry;
    private static final String GIT_HUB_REG_EXP = "^https?://github\\.com/([^/]+)/([^/]+)";
    private static final String GIT_HUB_API_EVENTS_URL = "/repos/{owner}/{repo}/events";

    @Autowired
    public GitHubClient(WebClient webClient, Retry retry) {
        this.webClient = webClient;
        this.retry = retry;
    }

    public GitHubEventsResponse.Event fetchRepository(String owner, String repo) {
        try {
            Mono<List<GitHubEventsResponse.Event>> request = webClient.get()
                .uri(GIT_HUB_API_EVENTS_URL, owner, repo)
                .retrieve()
                .onStatus(
                    HttpStatusCode::isError,
                    clientResponse -> Mono.error(new APIException(clientResponse.statusCode()))
                )
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
            return retry.executeSupplier(request::block).getFirst();
        } catch (WebClientRequestException | APIException e) {
            log.error(e.toString());
            return null;
        }
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
        try {
            Mono<List<GitHubEventsResponse.Event>> request =
                webClient.get()
                    .uri(GIT_HUB_API_EVENTS_URL, owner, repo)
                    .retrieve()
                    .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> Mono.error(new APIException(clientResponse.statusCode()))
                    )
                    .bodyToMono(new ParameterizedTypeReference<>() {
                    });
            List<GitHubEventsResponse.Event> events = retry.executeSupplier(request::block);
            return getDescriptionFromEvents(events, toDate);
        } catch (WebClientRequestException | APIException e) {
            log.error(e.toString());
            return "Описание недоступно(";
        }
    }

    private String getDescriptionFromEvents(List<GitHubEventsResponse.Event> events, OffsetDateTime toDate) {
        StringBuilder sb = new StringBuilder();
        for (GitHubEventsResponse.Event event : events) {
            if (event.getCreatedAt().isBefore(toDate) || event.getCreatedAt().equals(toDate)) {
                break;
            }
            sb.append("--");
            sb.append(event.toString());
            sb.append("\n");
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

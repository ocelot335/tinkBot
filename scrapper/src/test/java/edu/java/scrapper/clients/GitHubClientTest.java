package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.clients.apiclients.GitHubClient;
import edu.java.clients.responses.github.GitHubRepositoryResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SpringBootTest
public class GitHubClientTest {

    private static WireMockServer wireMockServer;
    private static WebClient webClient;

    @BeforeAll
    public static void setup() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        webClient = WebClient.builder()
            .baseUrl(wireMockServer.baseUrl())
            .build();
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testFetchRepository() {
        wireMockServer.stubFor(get(urlPathEqualTo("/repos/owner/repo"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(
                    "{\"name\": \"Test Repo\", \"description\": \"Test Description\", \"pushed_at\": \"2077-11-23T00:00:00Z\"}")
            ));

        GitHubClient gitHubClient = new GitHubClient(webClient);
        GitHubRepositoryResponse response = gitHubClient.fetchRepository("owner", "repo");

        Assertions.assertEquals("Test Repo", response.getName());
        Assertions.assertEquals("Test Description", response.getDescription());
        Assertions.assertEquals("2077-11-23T00:00Z", response.getPushedAt().toString());
    }
}

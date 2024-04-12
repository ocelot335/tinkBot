package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.clients.apiclients.StackOverflowClient;
import edu.java.clients.responses.StackOverflowResponse;
import edu.java.scrapper.IntegrationTest;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SpringBootTest
public class StackOverflowClientTest extends IntegrationTest {

    @Autowired
    private static WireMockServer wireMockServer;
    @Autowired
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
    public void testFetchQuestion() {
        wireMockServer.stubFor(get(urlPathEqualTo("/questions/42"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(
                    "{\"items\": [{\"title\": \"Test Question\", \"last_edit_date\": \"2004-11-23T00:00:00Z\", \"body\": \"Test Body\", \"last_activity_date\": \"2003-11-23T00:00:00Z\"}]}")
            ));

        StackOverflowClient stackOverflowClient = new StackOverflowClient(webClient, Retry.ofDefaults("test"));

        StackOverflowResponse response = stackOverflowClient.fetchQuestion("42");
        Assertions.assertEquals("Test Question", response.getItems().get(0).getTitle());
        Assertions.assertEquals("2003-11-23T00:00Z", response.getItems().get(0).getLastActivityDate().toString());
    }
}

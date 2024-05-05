package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.clients.apiclients.GitHubClient;
import edu.java.clients.responses.github.GitHubEventsResponse;
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
public class GitHubClientTest extends IntegrationTest {

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
    public void testFetchRepository() {
        wireMockServer.stubFor(get(urlPathEqualTo("/repos/owner/repo/events"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(
                    "[\n" +
                        "  {\n" +
                        "    \"id\": \"37345636865\",\n" +
                        "    \"type\": \"IssueCommentEvent\",\n" +
                        "    \"actor\": {\n" +
                        "      \"id\": 117304653,\n" +
                        "      \"login\": \"ocelot335\",\n" +
                        "      \"display_login\": \"ocelot335\",\n" +
                        "      \"gravatar_id\": \"\",\n" +
                        "      \"url\": \"https://api.github.com/users/ocelot335\",\n" +
                        "      \"avatar_url\": \"https://avatars.githubusercontent.com/u/117304653?\"\n" +
                        "    },\n" +
                        "    \"repo\": {\n" +
                        "      \"id\": 759454083,\n" +
                        "      \"name\": \"ocelot335/tinkBot\",\n" +
                        "      \"url\": \"https://api.github.com/repos/ocelot335/tinkBot\"\n" +
                        "    },\n" +
                        "    \"payload\": {\n" +
                        "      \"action\": \"created\",\n" +
                        "      \"issue\": {\n" +
                        "        \"url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8\",\n" +
                        "        \"repository_url\": \"https://api.github.com/repos/ocelot335/tinkBot\",\n" +
                        "        \"labels_url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8/labels{/name}\",\n" +
                        "        \"comments_url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8/comments\",\n" +
                        "        \"events_url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8/events\",\n" +
                        "        \"html_url\": \"https://github.com/ocelot335/tinkBot/issues/8\",\n" +
                        "        \"id\": 2229846363,\n" +
                        "        \"node_id\": \"I_kwDOLURZg86E6MFb\",\n" +
                        "        \"number\": 8,\n" +
                        "        \"title\": \"test2\",\n" +
                        "        \"user\": {\n" +
                        "          \"login\": \"ocelot335\",\n" +
                        "          \"id\": 117304653,\n" +
                        "          \"node_id\": \"U_kgDOBv3tTQ\",\n" +
                        "          \"avatar_url\": \"https://avatars.githubusercontent.com/u/117304653?v=4\",\n" +
                        "          \"gravatar_id\": \"\",\n" +
                        "          \"url\": \"https://api.github.com/users/ocelot335\",\n" +
                        "          \"html_url\": \"https://github.com/ocelot335\",\n" +
                        "          \"followers_url\": \"https://api.github.com/users/ocelot335/followers\",\n" +
                        "          \"following_url\": \"https://api.github.com/users/ocelot335/following{/other_user}\",\n" +
                        "          \"gists_url\": \"https://api.github.com/users/ocelot335/gists{/gist_id}\",\n" +
                        "          \"starred_url\": \"https://api.github.com/users/ocelot335/starred{/owner}{/repo}\",\n" +
                        "          \"subscriptions_url\": \"https://api.github.com/users/ocelot335/subscriptions\",\n" +
                        "          \"organizations_url\": \"https://api.github.com/users/ocelot335/orgs\",\n" +
                        "          \"repos_url\": \"https://api.github.com/users/ocelot335/repos\",\n" +
                        "          \"events_url\": \"https://api.github.com/users/ocelot335/events{/privacy}\",\n" +
                        "          \"received_events_url\": \"https://api.github.com/users/ocelot335/received_events\",\n" +
                        "          \"type\": \"User\",\n" +
                        "          \"site_admin\": false\n" +
                        "        },\n" +
                        "        \"labels\": [\n" +
                        "\n" +
                        "        ],\n" +
                        "        \"state\": \"open\",\n" +
                        "        \"locked\": false,\n" +
                        "        \"assignee\": null,\n" +
                        "        \"assignees\": [\n" +
                        "\n" +
                        "        ],\n" +
                        "        \"milestone\": null,\n" +
                        "        \"comments\": 23,\n" +
                        "        \"created_at\": \"2024-04-07T16:41:50Z\",\n" +
                        "        \"updated_at\": \"2024-04-10T17:08:27Z\",\n" +
                        "        \"closed_at\": null,\n" +
                        "        \"author_association\": \"OWNER\",\n" +
                        "        \"active_lock_reason\": null,\n" +
                        "        \"body\": null,\n" +
                        "        \"reactions\": {\n" +
                        "          \"url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8/reactions\",\n" +
                        "          \"total_count\": 0,\n" +
                        "          \"+1\": 0,\n" +
                        "          \"-1\": 0,\n" +
                        "          \"laugh\": 0,\n" +
                        "          \"hooray\": 0,\n" +
                        "          \"confused\": 0,\n" +
                        "          \"heart\": 0,\n" +
                        "          \"rocket\": 0,\n" +
                        "          \"eyes\": 0\n" +
                        "        },\n" +
                        "        \"timeline_url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8/timeline\",\n" +
                        "        \"performed_via_github_app\": null,\n" +
                        "        \"state_reason\": null\n" +
                        "      },\n" +
                        "      \"comment\": {\n" +
                        "        \"url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/comments/2048071717\",\n" +
                        "        \"html_url\": \"https://github.com/ocelot335/tinkBot/issues/8#issuecomment-2048071717\",\n" +
                        "        \"issue_url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8\",\n" +
                        "        \"id\": 2048071717,\n" +
                        "        \"node_id\": \"IC_kwDOLURZg856Exgl\",\n" +
                        "        \"user\": {\n" +
                        "          \"login\": \"ocelot335\",\n" +
                        "          \"id\": 117304653,\n" +
                        "          \"node_id\": \"U_kgDOBv3tTQ\",\n" +
                        "          \"avatar_url\": \"https://avatars.githubusercontent.com/u/117304653?v=4\",\n" +
                        "          \"gravatar_id\": \"\",\n" +
                        "          \"url\": \"https://api.github.com/users/ocelot335\",\n" +
                        "          \"html_url\": \"https://github.com/ocelot335\",\n" +
                        "          \"followers_url\": \"https://api.github.com/users/ocelot335/followers\",\n" +
                        "          \"following_url\": \"https://api.github.com/users/ocelot335/following{/other_user}\",\n" +
                        "          \"gists_url\": \"https://api.github.com/users/ocelot335/gists{/gist_id}\",\n" +
                        "          \"starred_url\": \"https://api.github.com/users/ocelot335/starred{/owner}{/repo}\",\n" +
                        "          \"subscriptions_url\": \"https://api.github.com/users/ocelot335/subscriptions\",\n" +
                        "          \"organizations_url\": \"https://api.github.com/users/ocelot335/orgs\",\n" +
                        "          \"repos_url\": \"https://api.github.com/users/ocelot335/repos\",\n" +
                        "          \"events_url\": \"https://api.github.com/users/ocelot335/events{/privacy}\",\n" +
                        "          \"received_events_url\": \"https://api.github.com/users/ocelot335/received_events\",\n" +
                        "          \"type\": \"User\",\n" +
                        "          \"site_admin\": false\n" +
                        "        },\n" +
                        "        \"created_at\": \"2024-04-10T17:08:26Z\",\n" +
                        "        \"updated_at\": \"2024-04-10T17:08:26Z\",\n" +
                        "        \"author_association\": \"OWNER\",\n" +
                        "        \"body\": \"test\",\n" +
                        "        \"reactions\": {\n" +
                        "          \"url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/comments/2048071717/reactions\",\n" +
                        "          \"total_count\": 0,\n" +
                        "          \"+1\": 0,\n" +
                        "          \"-1\": 0,\n" +
                        "          \"laugh\": 0,\n" +
                        "          \"hooray\": 0,\n" +
                        "          \"confused\": 0,\n" +
                        "          \"heart\": 0,\n" +
                        "          \"rocket\": 0,\n" +
                        "          \"eyes\": 0\n" +
                        "        },\n" +
                        "        \"performed_via_github_app\": null\n" +
                        "      }\n" +
                        "    },\n" +
                        "    \"public\": true,\n" +
                        "    \"created_at\": \"2024-04-10T17:08:27Z\"\n" +
                        "  },{\n" +
                        "    \"id\": \"37345473061\",\n" +
                        "    \"type\": \"IssueCommentEvent\",\n" +
                        "    \"actor\": {\n" +
                        "      \"id\": 117304653,\n" +
                        "      \"login\": \"ocelot335\",\n" +
                        "      \"display_login\": \"ocelot335\",\n" +
                        "      \"gravatar_id\": \"\",\n" +
                        "      \"url\": \"https://api.github.com/users/ocelot335\",\n" +
                        "      \"avatar_url\": \"https://avatars.githubusercontent.com/u/117304653?\"\n" +
                        "    },\n" +
                        "    \"repo\": {\n" +
                        "      \"id\": 759454083,\n" +
                        "      \"name\": \"ocelot335/tinkBot\",\n" +
                        "      \"url\": \"https://api.github.com/repos/ocelot335/tinkBot\"\n" +
                        "    },\n" +
                        "    \"payload\": {\n" +
                        "      \"action\": \"created\",\n" +
                        "      \"issue\": {\n" +
                        "        \"url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8\",\n" +
                        "        \"repository_url\": \"https://api.github.com/repos/ocelot335/tinkBot\",\n" +
                        "        \"labels_url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8/labels{/name}\",\n" +
                        "        \"comments_url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8/comments\",\n" +
                        "        \"events_url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8/events\",\n" +
                        "        \"html_url\": \"https://github.com/ocelot335/tinkBot/issues/8\",\n" +
                        "        \"id\": 2229846363,\n" +
                        "        \"node_id\": \"I_kwDOLURZg86E6MFb\",\n" +
                        "        \"number\": 8,\n" +
                        "        \"title\": \"test2\",\n" +
                        "        \"user\": {\n" +
                        "          \"login\": \"ocelot335\",\n" +
                        "          \"id\": 117304653,\n" +
                        "          \"node_id\": \"U_kgDOBv3tTQ\",\n" +
                        "          \"avatar_url\": \"https://avatars.githubusercontent.com/u/117304653?v=4\",\n" +
                        "          \"gravatar_id\": \"\",\n" +
                        "          \"url\": \"https://api.github.com/users/ocelot335\",\n" +
                        "          \"html_url\": \"https://github.com/ocelot335\",\n" +
                        "          \"followers_url\": \"https://api.github.com/users/ocelot335/followers\",\n" +
                        "          \"following_url\": \"https://api.github.com/users/ocelot335/following{/other_user}\",\n" +
                        "          \"gists_url\": \"https://api.github.com/users/ocelot335/gists{/gist_id}\",\n" +
                        "          \"starred_url\": \"https://api.github.com/users/ocelot335/starred{/owner}{/repo}\",\n" +
                        "          \"subscriptions_url\": \"https://api.github.com/users/ocelot335/subscriptions\",\n" +
                        "          \"organizations_url\": \"https://api.github.com/users/ocelot335/orgs\",\n" +
                        "          \"repos_url\": \"https://api.github.com/users/ocelot335/repos\",\n" +
                        "          \"events_url\": \"https://api.github.com/users/ocelot335/events{/privacy}\",\n" +
                        "          \"received_events_url\": \"https://api.github.com/users/ocelot335/received_events\",\n" +
                        "          \"type\": \"User\",\n" +
                        "          \"site_admin\": false\n" +
                        "        },\n" +
                        "        \"labels\": [\n" +
                        "\n" +
                        "        ],\n" +
                        "        \"state\": \"open\",\n" +
                        "        \"locked\": false,\n" +
                        "        \"assignee\": null,\n" +
                        "        \"assignees\": [\n" +
                        "\n" +
                        "        ],\n" +
                        "        \"milestone\": null,\n" +
                        "        \"comments\": 22,\n" +
                        "        \"created_at\": \"2024-04-07T16:41:50Z\",\n" +
                        "        \"updated_at\": \"2024-04-10T17:02:48Z\",\n" +
                        "        \"closed_at\": null,\n" +
                        "        \"author_association\": \"OWNER\",\n" +
                        "        \"active_lock_reason\": null,\n" +
                        "        \"body\": null,\n" +
                        "        \"reactions\": {\n" +
                        "          \"url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8/reactions\",\n" +
                        "          \"total_count\": 0,\n" +
                        "          \"+1\": 0,\n" +
                        "          \"-1\": 0,\n" +
                        "          \"laugh\": 0,\n" +
                        "          \"hooray\": 0,\n" +
                        "          \"confused\": 0,\n" +
                        "          \"heart\": 0,\n" +
                        "          \"rocket\": 0,\n" +
                        "          \"eyes\": 0\n" +
                        "        },\n" +
                        "        \"timeline_url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8/timeline\",\n" +
                        "        \"performed_via_github_app\": null,\n" +
                        "        \"state_reason\": null\n" +
                        "      },\n" +
                        "      \"comment\": {\n" +
                        "        \"url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/comments/2048060366\",\n" +
                        "        \"html_url\": \"https://github.com/ocelot335/tinkBot/issues/8#issuecomment-2048060366\",\n" +
                        "        \"issue_url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/8\",\n" +
                        "        \"id\": 2048060366,\n" +
                        "        \"node_id\": \"IC_kwDOLURZg856EuvO\",\n" +
                        "        \"user\": {\n" +
                        "          \"login\": \"ocelot335\",\n" +
                        "          \"id\": 117304653,\n" +
                        "          \"node_id\": \"U_kgDOBv3tTQ\",\n" +
                        "          \"avatar_url\": \"https://avatars.githubusercontent.com/u/117304653?v=4\",\n" +
                        "          \"gravatar_id\": \"\",\n" +
                        "          \"url\": \"https://api.github.com/users/ocelot335\",\n" +
                        "          \"html_url\": \"https://github.com/ocelot335\",\n" +
                        "          \"followers_url\": \"https://api.github.com/users/ocelot335/followers\",\n" +
                        "          \"following_url\": \"https://api.github.com/users/ocelot335/following{/other_user}\",\n" +
                        "          \"gists_url\": \"https://api.github.com/users/ocelot335/gists{/gist_id}\",\n" +
                        "          \"starred_url\": \"https://api.github.com/users/ocelot335/starred{/owner}{/repo}\",\n" +
                        "          \"subscriptions_url\": \"https://api.github.com/users/ocelot335/subscriptions\",\n" +
                        "          \"organizations_url\": \"https://api.github.com/users/ocelot335/orgs\",\n" +
                        "          \"repos_url\": \"https://api.github.com/users/ocelot335/repos\",\n" +
                        "          \"events_url\": \"https://api.github.com/users/ocelot335/events{/privacy}\",\n" +
                        "          \"received_events_url\": \"https://api.github.com/users/ocelot335/received_events\",\n" +
                        "          \"type\": \"User\",\n" +
                        "          \"site_admin\": false\n" +
                        "        },\n" +
                        "        \"created_at\": \"2024-04-10T17:02:47Z\",\n" +
                        "        \"updated_at\": \"2024-04-10T17:02:47Z\",\n" +
                        "        \"author_association\": \"OWNER\",\n" +
                        "        \"body\": \"test\",\n" +
                        "        \"reactions\": {\n" +
                        "          \"url\": \"https://api.github.com/repos/ocelot335/tinkBot/issues/comments/2048060366/reactions\",\n" +
                        "          \"total_count\": 0,\n" +
                        "          \"+1\": 0,\n" +
                        "          \"-1\": 0,\n" +
                        "          \"laugh\": 0,\n" +
                        "          \"hooray\": 0,\n" +
                        "          \"confused\": 0,\n" +
                        "          \"heart\": 0,\n" +
                        "          \"rocket\": 0,\n" +
                        "          \"eyes\": 0\n" +
                        "        },\n" +
                        "        \"performed_via_github_app\": null\n" +
                        "      }\n" +
                        "    },\n" +
                        "    \"public\": true,\n" +
                        "    \"created_at\": \"2024-04-10T17:02:48Z\"\n" +
                        "  }]")
            ));

        GitHubClient gitHubClient = new GitHubClient(webClient, Retry.ofDefaults("test"));
        GitHubEventsResponse.Event response = gitHubClient.fetchRepository("owner", "repo");

        Assertions.assertEquals("2024-04-10T17:08:27Z", response.getLastUpdatedAt().toString());
    }
}

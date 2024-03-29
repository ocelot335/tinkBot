package edu.java.clients.responses.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubEventsResponse {
    public List<Event> events;

    @Data
    public static class Event {

    }
}

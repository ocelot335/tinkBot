package edu.java.clients.responses.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubEventsResponse {
    private List<Event> events;

    @Data
    public static class Event {
        private String type;
        @JsonProperty("created_at")
        private OffsetDateTime createdAt;

        @Override
        public String toString() {
            String description = "Событие типа ";
            description += switch (type) {
                case "IssueCommentEvent" -> "\"Комментарий\"";
                case "PushEvent" -> "\"Пуш в репозиторий\"";
                case "PullRequestReviewCommentEvent" -> "\"Комментарий в обсуждении PR\"";
                case "PullRequestReviewEvent" -> "\"Начато новое обсуждение PR\"";
                case "CreateEvent" -> "\"Новая ветка или тэг\"";
                case "DeleteEvent" -> "\"Ветка или тэг удалены\"";
                case "IssuesEvent" -> "\"Новый тикет\"";
                case "ForkEvent" -> "\"Новый форк\"";
                case "CommitCommentEvent" -> "\"Новый комментарий к коммиту\"";
                default -> type;
            };
            description += " произошло в ";
            description += createdAt.toString();
            return description;
        }
    }
}

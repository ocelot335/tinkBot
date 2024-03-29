package edu.java.clients.responses.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.clients.responses.IAPIResponse;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepositoryResponse implements IAPIResponse {
    private String name;
    private String description;
    @JsonProperty("pushed_at")
    private OffsetDateTime pushedAt;

    @Override
    public OffsetDateTime getLastUpdatedAt() {
        return pushedAt;
    }
    //TODO::add fields
}

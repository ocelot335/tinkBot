package edu.java.clients.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StackOverflowResponse implements IAPIResponse {
    private List<Item> items;

    @Override
    public OffsetDateTime getLastUpdatedAt() {
        return items.get(0).lastActivityDate;
    }

    @Data
    public static class Item {
        private String title;

        @JsonProperty("last_activity_date")
        private OffsetDateTime lastActivityDate;
        //TODO::add more fields
    }
}

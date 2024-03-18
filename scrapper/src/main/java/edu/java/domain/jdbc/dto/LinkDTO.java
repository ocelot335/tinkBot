package edu.java.domain.jdbc.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkDTO {
    Long id;
    String url;
    OffsetDateTime createdAt;

    public LinkDTO(String url) {
        this.url = url;
    }
}

package edu.java.domain.jdbc.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubscribeDTO {
    Long chatId;
    Long linkId;
}

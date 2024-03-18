package edu.java.domain.jdbc.dto;

import lombok.Data;

@Data
public class ChatDTO {
    Long telegramId;


    public ChatDTO(Long telegramId) {
        this.telegramId = telegramId;
    }
}

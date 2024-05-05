package edu.java.bot.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkUpdate {
    Long id;

    @NotEmpty
    String url;

    String description;

    @NotEmpty
    List<Long> tgChatIds;
}

package edu.java.controller.dto;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class AddLinkRequest {
    @URL
    String link;
}

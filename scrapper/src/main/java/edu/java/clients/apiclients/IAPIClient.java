package edu.java.clients.apiclients;

import edu.java.clients.responses.IAPIResponse;
import edu.java.domain.dto.LinkDTO;

public interface IAPIClient {
    IAPIResponse getResponse(LinkDTO link);

    boolean isCorrectURL(String url);
}

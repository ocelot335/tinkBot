package edu.java.clients.apiclients;

import edu.java.clients.responses.IAPIResponse;
import edu.java.domain.jdbc.dto.LinkDTO;

public interface IAPIClient {
    public IAPIResponse getResponse(LinkDTO link);
    boolean isCorrectURL(String url);
}

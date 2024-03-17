package edu.java.clients;

import edu.java.clients.dto.ApiErrorResponse;
import java.util.List;
import lombok.Data;

@Data
public class BotApiException extends RuntimeException {
    private final String description;
    private final String code;
    private final String exceptionName;
    private final String exceptionMessage;
    private final List<String> apiExceptionStackTrace;

    public BotApiException(ApiErrorResponse apiErrorResponse) {
        this.description = apiErrorResponse.getDescription();
        this.code = apiErrorResponse.getCode();
        this.exceptionName = apiErrorResponse.getExceptionName();
        this.exceptionMessage = apiErrorResponse.getExceptionMessage();
        this.apiExceptionStackTrace = apiErrorResponse.getStackTrace();
    }
}

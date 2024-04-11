package edu.java.clients.apiclients;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Data
public class APIException extends RuntimeException {
    String code;

    public APIException(HttpStatusCode code) {
        this.code = String.valueOf(code.value());
    }
}

package edu.java.controller.exception;

public class ChatNotFoundException extends RuntimeException implements IAPIError {
    public ChatNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getDescription() {
        return "Сначала надо пройти регистрацию";
    }

    @Override
    public String getCode() {
        return "404";
    }

    @Override
    public String getName() {
        return "ChatNotFoundException";
    }
}

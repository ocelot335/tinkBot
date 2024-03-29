package edu.java.services.jdbc;

import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.ChatReAddingException;
import edu.java.domain.jdbc.JdbcChatsDAO;
import edu.java.services.interfaces.ITgChatService;

public class JdbcTgChatService implements ITgChatService {
    JdbcChatsDAO chatRepository;

    public JdbcTgChatService(JdbcChatsDAO chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public void addUser(Long chatId) {
        if (chatRepository.contains(chatId)) {
            throw new ChatReAddingException("Пользователь со следующим id уже добавлен: " + chatId);
        }
        chatRepository.add(chatId);
    }

    @Override
    public void remove(Long chatId) {
        if (!chatRepository.contains(chatId)) {
            throw new ChatNotFoundException("Нет чата с id: " + chatId);
        }
        chatRepository.remove(chatId);
    }
}

package edu.java.services.jpa;

import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.ChatReAddingException;
import edu.java.domain.dto.ChatDTO;
import edu.java.domain.jpa.JpaChatsDAO;
import edu.java.domain.jpa.entities.ChatEntity;
import edu.java.services.interfaces.ITgChatService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JpaTgChatService implements ITgChatService {
    JpaChatsDAO chatRepository;

    public JpaTgChatService(JpaChatsDAO chatsRepository) {
        this.chatRepository = chatsRepository;
    }

    @Override
    public void addUser(Long chatId) {
        if (chatRepository.existsById(chatId)) {
            throw new ChatReAddingException("Пользователь со следующим id уже добавлен: " + chatId);
        }
        chatRepository.saveAndFlush(new ChatEntity(chatId));
    }

    @Override
    public void remove(Long chatId) {
        if (!chatRepository.existsById(chatId)) {
            throw new ChatNotFoundException("Нет чата с id: " + chatId);
        }
        chatRepository.deleteById(chatId);
    }
}

package edu.java.services.jooq;

import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.ChatReAddingException;
import edu.java.domain.jooq.JooqChatsDAO;
import edu.java.services.interfaces.ITgChatService;

//Этот сервис вообще никак не отличается от того, который был для jdbc, непонятно зачем вообще
// делать реализации для сервисов, главное кажется ведь для репозиториев?
public class JooqTgChatService implements ITgChatService {
    JooqChatsDAO chatRepository;

    public JooqTgChatService(JooqChatsDAO chatRepository) {
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

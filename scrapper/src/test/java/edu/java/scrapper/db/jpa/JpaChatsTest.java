package edu.java.scrapper.db.jpa;

import edu.java.domain.jpa.JpaChatsDAO;
import edu.java.domain.jpa.entities.ChatEntity;
import edu.java.scrapper.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class JpaChatsTest extends IntegrationTest {
    @Autowired
    private JpaChatsDAO chatRepository;

    @Test
    @Transactional
    @Rollback
    void addTest() {
        chatRepository.saveByTelegramId(1L);
        Assertions.assertEquals(1L, chatRepository.findAll().get(0).getTelegramId());
        Assertions.assertEquals(1, chatRepository.findAll().size());

        chatRepository.saveByTelegramId(2L);
        List<ChatEntity> chats = chatRepository.findAll();
        Assertions.assertEquals(2, chats.size());
        Assertions.assertTrue(1L == (chats.get(0).getTelegramId())
            || 1L == (chats.get(1).getTelegramId()));
        Assertions.assertTrue(2L == (chats.get(0).getTelegramId())
            || 2L == (chats.get(1).getTelegramId()));
    }
}

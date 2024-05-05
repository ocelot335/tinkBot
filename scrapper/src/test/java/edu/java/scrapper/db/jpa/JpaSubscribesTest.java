package edu.java.scrapper.db.jpa;

import edu.java.domain.jpa.JpaChatsDAO;
import edu.java.domain.jpa.JpaLinksDAO;
import edu.java.domain.jpa.entities.ChatEntity;
import edu.java.domain.jpa.entities.LinkEntity;
import edu.java.scrapper.IntegrationTest;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class JpaSubscribesTest extends IntegrationTest {

    @Autowired
    private JpaLinksDAO linkRepository;

    @Autowired
    private JpaChatsDAO chatRepository;

    @Test
    @Transactional
    @Rollback
    void addTestFirstFromChat() {
        chatRepository.saveByTelegramId(1L);
        linkRepository.saveByUrl("java");
        LinkEntity link = linkRepository.findAll().get(0);
        ChatEntity chat = chatRepository.findById(1L).get();
        chat.addSubscribe(link);
        Assertions.assertEquals(1, link.getUsersToNotify().size());
        Assertions.assertEquals(1L, link.getUsersToNotify().get(0).getTelegramId());
        Assertions.assertEquals(
            link.getId(),
            chat.getSubscribes().get(0).getId()
        );
    }

    @Test
    @Transactional
    @Rollback
    void removeTest() {
        chatRepository.saveByTelegramId(1L);
        linkRepository.saveByUrl("java");
        LinkEntity link = linkRepository.findAll().get(0);
        ChatEntity chat = chatRepository.findById(1L).get();

        chat.addSubscribe(link);

        Assertions.assertEquals(1, chat.getSubscribes().size());
        chat.getSubscribes().remove(link);
        Assertions.assertEquals(0, chat.getSubscribes().size());
    }

    @Test
    @Transactional
    @Rollback
    void testContains() {
        chatRepository.saveByTelegramId(1L);
        linkRepository.saveByUrl("java");
        LinkEntity link = linkRepository.findAll().get(0);
        ChatEntity chat = chatRepository.findById(1L).get();

        Assertions.assertFalse(link.getUsersToNotify().contains(chat));
        chat.addSubscribe(link);
        Assertions.assertTrue(link.getUsersToNotify().contains(chat));
        chat.removeSubscribe(link);
        Assertions.assertFalse(link.getUsersToNotify().contains(chat));
    }

    @Test
    @Transactional
    @Rollback
    void testFindAllByChats() {
        chatRepository.saveByTelegramId(1L);
        chatRepository.saveByTelegramId(2L);
        linkRepository.saveByUrl("java");
        linkRepository.saveByUrl("kotlin");
        Long linkIdJava = linkRepository.getId("java");
        Long linkIdKotlin = linkRepository.getId("kotlin");

        LinkEntity linkJava = linkRepository.findById(linkIdJava).get();
        LinkEntity linkKotlin = linkRepository.findById(linkIdKotlin).get();
        ChatEntity chat1 = chatRepository.findById(1L).get();
        ChatEntity chat2 = chatRepository.findById(2L).get();
        chat1.addSubscribe(linkJava);
        chat1.addSubscribe(linkKotlin);
        chat2.addSubscribe(linkKotlin);
        List<LinkEntity> linksFirst = chat1.getSubscribes();
        Assertions.assertTrue(Objects.equals(linksFirst.get(0).getId(), linkIdKotlin) ||
            Objects.equals(linksFirst.get(1).getId(), linkIdKotlin));
        Assertions.assertTrue(Objects.equals(linksFirst.get(0).getId(), linkIdJava) ||
            Objects.equals(linksFirst.get(1).getId(), linkIdJava));
        Assertions.assertEquals(2, linksFirst.size());

        List<LinkEntity> linksSecond = chat2.getSubscribes();
        Assertions.assertEquals(linksSecond.get(0).getId(), linkIdKotlin);
        Assertions.assertEquals(1, linksSecond.size());
    }

    @Test
    @Transactional
    @Rollback
    void testFindAllByLinks() {
        chatRepository.saveByTelegramId(1L);
        chatRepository.saveByTelegramId(2L);
        linkRepository.saveByUrl("java");
        linkRepository.saveByUrl("kotlin");
        Long linkIdJava = linkRepository.getId("java");
        Long linkIdKotlin = linkRepository.getId("kotlin");

        LinkEntity linkJava = linkRepository.findById(linkIdJava).get();
        LinkEntity linkKotlin = linkRepository.findById(linkIdKotlin).get();
        ChatEntity chat1 = chatRepository.findById(1L).get();
        ChatEntity chat2 = chatRepository.findById(2L).get();
        chat1.addSubscribe(linkJava);
        chat1.addSubscribe(linkKotlin);
        chat2.addSubscribe(linkKotlin);
        List<ChatEntity> linksFirst = linkKotlin.getUsersToNotify();
        Assertions.assertTrue(Objects.equals(linksFirst.get(0).getTelegramId(), 1L) ||
            Objects.equals(linksFirst.get(1).getTelegramId(), 1L));
        Assertions.assertTrue(Objects.equals(linksFirst.get(0).getTelegramId(), 2L) ||
            Objects.equals(linksFirst.get(1).getTelegramId(), 2L));
        Assertions.assertEquals(2, linksFirst.size());

        List<ChatEntity> linksSecond = linkJava.getUsersToNotify();
        Assertions.assertEquals(linksSecond.get(0).getTelegramId(), 1L);
        Assertions.assertEquals(1, linksSecond.size());
    }

    @Test
    @Transactional
    @Rollback
    void testGetIdsOfUsersToNotifyByLinkId() {
        chatRepository.saveByTelegramId(1L);
        chatRepository.saveByTelegramId(2L);
        chatRepository.saveByTelegramId(3L);
        linkRepository.saveByUrl("java");
        LinkEntity link = linkRepository.findAll().get(0);
        ChatEntity chat1 = chatRepository.findById(1L).get();
        ChatEntity chat2 = chatRepository.findById(2L).get();
        ChatEntity chat3 = chatRepository.findById(3L).get();
        chat1.addSubscribe(link);
        chat2.addSubscribe(link);
        chat3.addSubscribe(link);
        List<Long> chatIds = linkRepository.getIdsOfUsersToNotifyByLinkId(link.getId());
        Assertions.assertTrue(chatIds.contains(1L));
        Assertions.assertTrue(chatIds.contains(2L));
        Assertions.assertTrue(chatIds.contains(3L));
        Assertions.assertEquals(3, chatIds.size());

        chat1.removeSubscribe(link);
        chatIds = linkRepository.getIdsOfUsersToNotifyByLinkId(link.getId());
        Assertions.assertTrue(chatIds.contains(2L));
        Assertions.assertTrue(chatIds.contains(3L));
        Assertions.assertEquals(2, chatIds.size());
    }
}

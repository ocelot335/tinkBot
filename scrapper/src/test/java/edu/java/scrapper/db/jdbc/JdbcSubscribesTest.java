package edu.java.scrapper.db.jdbc;

import edu.java.domain.jdbc.JdbcChatsDAO;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jdbc.JdbcSubscribesDAO;
import edu.java.domain.dto.LinkDTO;
import edu.java.domain.dto.SubscribeDTO;
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
public class JdbcSubscribesTest extends IntegrationTest {
    @Autowired
    private JdbcLinksDAO linkRepository;

    @Autowired
    private JdbcChatsDAO chatRepository;

    @Autowired
    private JdbcSubscribesDAO subscribeRepository;

    @Test
    @Transactional
    @Rollback
    void addTest() {
        linkRepository.add("java");
        chatRepository.add(1L);
        Long linkId = linkRepository.findAll().get(0).getId();

        subscribeRepository.add(1L, linkId);
        Assertions.assertEquals(1, subscribeRepository.findAllSubscribes().size());
        Assertions.assertEquals(1L, subscribeRepository.findAllSubscribes().get(0).getChatId());
        Assertions.assertEquals(
            linkId,
            subscribeRepository.findAllSubscribes().get(0).getLinkId()
        );
    }

    @Test
    @Transactional
    @Rollback
    void removeTest() {
        linkRepository.add("java");
        chatRepository.add(1L);
        Long linkId = linkRepository.findAll().get(0).getId();

        subscribeRepository.add(1L, linkId);
        Assertions.assertEquals(1, subscribeRepository.findAllSubscribes().size());
        subscribeRepository.remove(1L, linkId);
        Assertions.assertEquals(0, subscribeRepository.findAllSubscribes().size());
    }

    @Test
    @Transactional
    @Rollback
    void testForeignKeyChat() {
        linkRepository.add("java");
        chatRepository.add(1L);
        Long linkId = linkRepository.findAll().get(0).getId();

        subscribeRepository.add(1L, linkId);
        Assertions.assertEquals(1, subscribeRepository.findAllSubscribes().size());
        chatRepository.remove(1L);
        Assertions.assertEquals(0, subscribeRepository.findAllSubscribes().size());
    }

    @Test
    @Transactional
    @Rollback
    void testContains() {
        linkRepository.add("java");
        chatRepository.add(1L);
        Long linkId = linkRepository.getId("java");
        Assertions.assertFalse(subscribeRepository.contains(1L, linkId));
        subscribeRepository.add(1L, linkId);
        Assertions.assertTrue(subscribeRepository.contains(1L, linkId));
        subscribeRepository.remove(1L, linkId);
        Assertions.assertFalse(subscribeRepository.contains(1L, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void testFindAll() {
        linkRepository.add("java");
        linkRepository.add("kotlin");
        chatRepository.add(1L);
        chatRepository.add(2L);
        Long linkIdJava = linkRepository.getId("java");
        Long linkIdKotlin = linkRepository.getId("kotlin");
        subscribeRepository.add(1L, linkIdJava);
        subscribeRepository.add(1L, linkIdKotlin);
        subscribeRepository.add(2L, linkIdKotlin);
        List<LinkDTO> linksFirst = subscribeRepository.findAllLinksByChatId(1L);
        Assertions.assertTrue(Objects.equals(linksFirst.get(0).getId(), linkIdKotlin) ||
            Objects.equals(linksFirst.get(1).getId(), linkIdKotlin));
        Assertions.assertTrue(Objects.equals(linksFirst.get(0).getId(), linkIdJava) ||
            Objects.equals(linksFirst.get(1).getId(), linkIdJava));
        Assertions.assertEquals(2, linksFirst.size());

        List<LinkDTO> linksSecond = subscribeRepository.findAllLinksByChatId(2L);
        Assertions.assertEquals(linksSecond.get(0).getId(), linkIdKotlin);
        Assertions.assertEquals(1, linksSecond.size());

        List<SubscribeDTO> links = subscribeRepository.findAllSubscribes();
        Assertions.assertEquals(3, links.size());
    }
}

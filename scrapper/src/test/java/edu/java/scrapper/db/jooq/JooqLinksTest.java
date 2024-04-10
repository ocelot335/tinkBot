package edu.java.scrapper.db.jooq;

import edu.java.domain.dto.LinkDTO;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jooq.JooqLinksDAO;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@SpringBootTest
public class JooqLinksTest extends IntegrationTest {
    @Autowired
    private JooqLinksDAO linkRepository;

    @Test
    @Transactional
    @Rollback
    void addTest() {
        linkRepository.add("http://java.com");
        Assertions.assertEquals("http://java.com", linkRepository.findAll().get(0).getUrl());
        Assertions.assertEquals(1, linkRepository.findAll().size());

        linkRepository.add("http://kotlin.com");
        List<LinkDTO> links = linkRepository.findAll();
        Assertions.assertEquals(2, links.size());
        Assertions.assertTrue("http://kotlin.com".equals(links.get(0).getUrl())
            || "http://kotlin.com".equals(links.get(1).getUrl()));
        Assertions.assertTrue("http://java.com".equals(links.get(0).getUrl())
            || "http://kotlin.com".equals(links.get(1).getUrl()));
    }

    @Test
    @Transactional
    @Rollback
    void removeTest() {
        linkRepository.add("http://java.com");
        linkRepository.add("http://kotlin.com");

        List<LinkDTO> links = linkRepository.findAll();

        Assertions.assertEquals(2, links.size());

        linkRepository.remove("http://java.com");
        Assertions.assertEquals(1, linkRepository.findAll().size());
        linkRepository.remove("http://kotlin.com");

        links = linkRepository.findAll();
        Assertions.assertTrue(links.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void containsTest() {
        Assertions.assertFalse(linkRepository.contains("java"));
        linkRepository.add("java");
        Assertions.assertTrue(linkRepository.contains("java"));
        linkRepository.remove("java");
        Assertions.assertFalse(linkRepository.contains("java"));
    }

    @Test
    @Transactional
    @Rollback
    void getIdTest() {
        linkRepository.add("java");
        Assertions.assertEquals(linkRepository.findAll().get(0).getId(), linkRepository.getId("java"));
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLastUpdate() {
        linkRepository.add("http://java.com");

        LinkDTO link = linkRepository.findAll().stream()
            .filter(link1 -> link1.getUrl().equals("http://java.com"))
            .findFirst()
            .orElseThrow();

        OffsetDateTime updatedTimeToUpdate = link.getLastUpdatedAt().plus(Duration.ofHours(1));
        linkRepository.updateLastUpdate(link, updatedTimeToUpdate);

        LinkDTO updatedLink = linkRepository.findAll().stream()
            .filter(link1 -> link1.getUrl().equals(link.getUrl()))
            .findFirst()
            .orElseThrow();

        Assertions.assertEquals(updatedTimeToUpdate, updatedLink.getLastUpdatedAt());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateCheckedAt() {
        linkRepository.add("http://java.com");
        LinkDTO link = linkRepository.findAll().stream()
            .filter(link1 -> link1.getUrl().equals("http://java.com"))
            .findFirst()
            .orElseThrow();

        OffsetDateTime updatedTimeToUpdate = link.getCheckedAt().plus(Duration.ofHours(1));
        linkRepository.updateCheckedAt(link, updatedTimeToUpdate);

        LinkDTO updatedLink = linkRepository.findAll().stream()
            .filter(link1 -> link1.getUrl().equals(link.getUrl()))
            .findFirst()
            .orElseThrow();

        Assertions.assertEquals(updatedTimeToUpdate, updatedLink.getCheckedAt());
    }

    @Test
    @Transactional
    @Rollback
    void findAllFilteredToCheckTest() {
        linkRepository.add("http://java.com");
        linkRepository.add("http://kotlin.com");
        linkRepository.add("http://scala.com");

        LinkDTO javaLink = linkRepository.findAll().stream()
            .filter(link -> link.getUrl().equals("http://java.com"))
            .findFirst()
            .orElseThrow();
        linkRepository.updateCheckedAt(javaLink, OffsetDateTime.now().minus(Duration.ofMinutes(10)));

        LinkDTO kotlinLink = linkRepository.findAll().stream()
            .filter(link -> link.getUrl().equals("http://kotlin.com"))
            .findFirst()
            .orElseThrow();
        linkRepository.updateCheckedAt(kotlinLink, OffsetDateTime.now().minus(Duration.ofMinutes(5)));

        Duration forceCheckDelay = Duration.ofMinutes(4);
        List<LinkDTO> linksToCheck = linkRepository.findAllFilteredToCheck(forceCheckDelay);

        Assertions.assertEquals(2, linksToCheck.size());
        List<String> urls = linksToCheck.stream().map(LinkDTO::getUrl).toList();
        Assertions.assertTrue(urls.contains("http://java.com"));
        Assertions.assertTrue(urls.contains("http://kotlin.com"));

        forceCheckDelay = Duration.ofMinutes(7);
        linksToCheck = linkRepository.findAllFilteredToCheck(forceCheckDelay);

        Assertions.assertEquals(1, linksToCheck.size());
        Assertions.assertEquals("http://java.com", linksToCheck.get(0).getUrl());

        forceCheckDelay = Duration.ofMinutes(15);
        linksToCheck = linkRepository.findAllFilteredToCheck(forceCheckDelay);

        Assertions.assertEquals(0, linksToCheck.size());
    }
}

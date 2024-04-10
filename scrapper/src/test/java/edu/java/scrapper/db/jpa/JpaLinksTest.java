package edu.java.scrapper.db.jpa;

import edu.java.domain.dto.LinkDTO;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jpa.JpaLinksDAO;
import edu.java.domain.jpa.entities.LinkEntity;
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
public class JpaLinksTest extends IntegrationTest {
    @Autowired
    private JpaLinksDAO linkRepository;

    @Test
    @Transactional
    @Rollback
    void addTest() {
        linkRepository.saveByUrl("http://java.com");
        Assertions.assertEquals("http://java.com", linkRepository.findAll().get(0).getUrl());
        Assertions.assertEquals(1, linkRepository.findAll().size());

        linkRepository.saveByUrl("http://kotlin.com");
        List<LinkEntity> links = linkRepository.findAll();
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
        linkRepository.saveByUrl("http://java.com");
        linkRepository.saveByUrl("http://kotlin.com");

        List<LinkEntity> links = linkRepository.findAll();

        Assertions.assertEquals(2, links.size());

        linkRepository.removeByUrl("http://java.com");
        Assertions.assertEquals(1, linkRepository.findAll().size());
        linkRepository.removeByUrl("http://kotlin.com");

        links = linkRepository.findAll();
        Assertions.assertTrue(links.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void containsTest() {
        Assertions.assertFalse(linkRepository.existsByUrl("java"));
        linkRepository.saveByUrl("java");
        Assertions.assertTrue(linkRepository.existsByUrl("java"));
        linkRepository.removeByUrl("java");
        Assertions.assertFalse(linkRepository.existsByUrl("java"));
    }

    @Test
    @Transactional
    @Rollback
    void getIdTest() {
        linkRepository.saveByUrl("java");
        Assertions.assertEquals(linkRepository.findAll().get(0).getId(), linkRepository.getId("java"));
    }

    @Test
    @Transactional
    @Rollback
    void findAllFilteredToCheckTest() {
        linkRepository.saveByUrl("http://java.com");
        linkRepository.saveByUrl("http://kotlin.com");
        linkRepository.saveByUrl("http://scala.com");

        LinkEntity javaLink = linkRepository.findAll().stream()
            .filter(link -> link.getUrl().equals("http://java.com"))
            .findFirst()
            .orElseThrow();
        javaLink.setCheckedAt(OffsetDateTime.now().minus(Duration.ofMinutes(10)));
        linkRepository.save(javaLink);

        LinkEntity kotlinLink = linkRepository.findAll().stream()
            .filter(link -> link.getUrl().equals("http://kotlin.com"))
            .findFirst()
            .orElseThrow();
        kotlinLink.setCheckedAt(OffsetDateTime.now().minus(Duration.ofMinutes(5)));
        linkRepository.save(kotlinLink);

        Duration forceCheckDelay = Duration.ofMinutes(4);
        List<LinkEntity> linksToCheck = linkRepository.findAllFilteredToCheck(OffsetDateTime.now().minus(forceCheckDelay));

        Assertions.assertEquals(2, linksToCheck.size());
        List<String> urls = linksToCheck.stream().map(LinkEntity::getUrl).toList();
        Assertions.assertTrue(urls.contains("http://java.com"));
        Assertions.assertTrue(urls.contains("http://kotlin.com"));

        forceCheckDelay = Duration.ofMinutes(7);
        linksToCheck = linkRepository.findAllFilteredToCheck(OffsetDateTime.now().minus(forceCheckDelay));

        Assertions.assertEquals(1, linksToCheck.size());
        Assertions.assertEquals("http://java.com", linksToCheck.get(0).getUrl());

        forceCheckDelay = Duration.ofMinutes(15);
        linksToCheck = linkRepository.findAllFilteredToCheck(OffsetDateTime.now().minus(forceCheckDelay));

        Assertions.assertEquals(0, linksToCheck.size());
    }
}

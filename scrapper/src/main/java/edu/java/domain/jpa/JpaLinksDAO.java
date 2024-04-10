package edu.java.domain.jpa;

import edu.java.domain.jpa.entities.ChatEntity;
import edu.java.domain.jpa.entities.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface JpaLinksDAO extends JpaRepository<LinkEntity, Long> {
    boolean existsByUrl(String url);

    @Modifying
    @Transactional
    @Query("INSERT INTO LinkEntity(url) VALUES(?1)")
    void saveByUrl(String url);

    @Modifying
    @Transactional
    @Query("DELETE FROM LinkEntity WHERE url=?1")
    void removeByUrl(String url);

    @Query("SELECT id FROM LinkEntity WHERE url=?1")
    Long getId(String url);

    @Query("FROM LinkEntity WHERE checkedAt <= ?1")
    List<LinkEntity> findAllFilteredToCheck(OffsetDateTime checkBoundary);

    @Query("SELECT u.telegramId FROM LinkEntity l JOIN l.usersToNotify u WHERE l.id=?1")
    List<Long> getIdsOfUsersToNotifyByLinkId(Long linkId);
}

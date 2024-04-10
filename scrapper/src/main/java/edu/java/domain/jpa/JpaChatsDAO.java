package edu.java.domain.jpa;

import edu.java.domain.jpa.entities.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaChatsDAO extends JpaRepository<ChatEntity, Long> {
    @Modifying
    @Transactional
    @Query("INSERT INTO ChatEntity(telegramId) VALUES(?1)")
    void saveByTelegramId(Long id);
}

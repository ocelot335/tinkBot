package edu.java.domain.jpa;

import edu.java.domain.dto.ChatDTO;
import edu.java.domain.jpa.entities.ChatEntity;
import edu.java.domain.jpa.entities.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatsDAO extends JpaRepository<ChatEntity, Long> {
}

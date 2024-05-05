package edu.java.domain.jpa.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "links")
@NoArgsConstructor
public class LinkEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "url")
    String url;

    @Column(name = "checked_at")
    OffsetDateTime checkedAt;

    @Column(name = "last_updated_at")
    OffsetDateTime lastUpdatedAt;

    @ManyToMany(mappedBy = "subscribes", cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    List<ChatEntity> usersToNotify;
}

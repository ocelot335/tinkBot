package edu.java.domain.jpa.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@Table(name = "chats")
@NoArgsConstructor
public class ChatEntity {
    @Id
    @Column(name = "telegramid")
    Long telegramId;

    public ChatEntity(Long telegramId) {
        this.telegramId = telegramId;
    }

    @ManyToMany(cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    @JoinTable(
        name = "subscribes",
        joinColumns = @JoinColumn(name = "chatid"),
        inverseJoinColumns = @JoinColumn(name = "linkid"))
    List<LinkEntity> subscribes;

    public void addSubscribe(LinkEntity link) {
        link.getUsersToNotify().add(this);
        subscribes.add(link);
    }

    public void removeSubscribe(LinkEntity link) {
        link.getUsersToNotify().remove(this);
        subscribes.remove(link);
    }
}

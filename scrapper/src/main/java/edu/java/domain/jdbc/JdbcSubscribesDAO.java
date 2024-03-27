package edu.java.domain.jdbc;

import edu.java.domain.jdbc.dto.LinkDTO;
import edu.java.domain.jdbc.dto.SubscribeDTO;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcSubscribesDAO {
    JdbcClient jdbcClient;
    private final String linkIdColumnName = "linkId";
    private final String chatIdColumnName = "chatId";

    public JdbcSubscribesDAO(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional
    public void add(Long chatId, Long urlId) {
        //На каком этапе надо добавлять ссылку в таблицу ссылок, когда она добавляется в первый раз?
        //Сейчас это будет делаться у меня в сервисе

        String query = "INSERT INTO subscribes(chatId,linkId) VALUES(?,?);";
        jdbcClient.sql(query).param(chatId).param(urlId).update();
    }

    @Transactional
    public void remove(Long chatId, Long urlId) {
        String query = "DELETE FROM subscribes WHERE chatId=? AND linkId=?;";
        jdbcClient.sql(query).param(chatId).param(urlId).update();
    }

    //Нужна ли поддержка сериализации?, нужен ли Serializable?
    public List<SubscribeDTO> findAllSubscribes() {
        String query = "SELECT * FROM subscribes;";
        return jdbcClient.sql(query).query((rs, rowNum) ->
            SubscribeDTO.builder().chatId(rs.getLong(chatIdColumnName))
                .linkId(rs.getLong(linkIdColumnName)).build()).list();
    }

    //Это ок, делать такой метод? Просто кажется,
    // лучше что пусть лучше бд будет обрабатывать это,
    // чем если сервер будет сначала принимать
    // все записи, а потом фильтровать их джойнить.
    //
    // У нас ведь это нужно только по запросу пользователя.
    public List<LinkDTO> findAllLinksByChatId(Long chatId) {
        String query = "SELECT * FROM links WHERE links.id IN " +
            "(SELECT subscribes.linkId FROM subscribes WHERE chatId=?);";
        return jdbcClient.sql(query).param(chatId).query((rs, rowNum) ->
            new LinkDTO(rs.getLong("id"), rs.getString("url"),
                rs.getObject("checked_at", OffsetDateTime.class),
                rs.getObject("last_updated_at", OffsetDateTime.class)
            )).list();
    }

    //Это ок, делать такой метод?
    public boolean contains(Long chatId, Long urlId) {
        String query = "SELECT COUNT(*) FROM subscribes WHERE chatId=? AND linkId=?";
        int count = jdbcClient.sql(query)
            .param(chatId)
            .param(urlId)
            .query(Integer.class).single();
        return count > 0;
    }
}

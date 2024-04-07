package edu.java.domain.jdbc;

import edu.java.domain.dto.LinkDTO;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcLinksDAO {
    JdbcClient jdbcClient;
    private static final String URL_COLUMN_NAME = "url";
    private static final String CHECKED_AT_COLUMN_NAME = "checked_at";
    private static final String LAST_UPDATED_AT_COLUMN_NAME = "last_updated_at";

    @Autowired
    public JdbcLinksDAO(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional
    public void add(String url) {
        String query = "INSERT INTO links(url) VALUES(?);";
        jdbcClient.sql(query).param(url).update();
    }

    @Transactional
    public void remove(String url) {
        String query = "DELETE FROM links WHERE url=?;";
        jdbcClient.sql(query).param(url).update();
    }

    //Мне также вообще не нравится, что здесь я указываю явные имена стобцов, может их как-то можно инъектить?
    //Как я понимаю JdbcClient это не поддерживает?
    @Transactional(readOnly = true)
    public List<LinkDTO> findAll() {
        String query = "SELECT * FROM links;";
        return jdbcClient.sql(query).query((rs, rowNum) ->
            new LinkDTO(rs.getLong("id"), rs.getString(URL_COLUMN_NAME),
                rs.getObject(CHECKED_AT_COLUMN_NAME, OffsetDateTime.class),
                rs.getObject(LAST_UPDATED_AT_COLUMN_NAME, OffsetDateTime.class)
            )).list();
    }

    @Transactional(readOnly = true)
    public List<LinkDTO> findAllFilteredToCheck(Duration forceCheckDelay) {
        String interval = "'" + forceCheckDelay.getSeconds() + " seconds'";
        String query = "SELECT * FROM links WHERE checked_at + interval " + interval + " <= NOW();";
        return jdbcClient.sql(query).query((rs, rowNum) ->
            new LinkDTO(rs.getLong("id"), rs.getString(URL_COLUMN_NAME),
                rs.getObject(CHECKED_AT_COLUMN_NAME, OffsetDateTime.class),
                rs.getObject(LAST_UPDATED_AT_COLUMN_NAME, OffsetDateTime.class)
            )).list();
    }

    //Это ок, делать такой метод?
    @Transactional(readOnly = true)
    public Long getId(String url) {
        String queryGetUrlId = "SELECT id FROM links WHERE url=?";
        return jdbcClient.sql(queryGetUrlId).param(url).query((rs, rowNum) ->
            rs.getLong("id")).single();
    }

    //Это ок, делать такой метод?
    @Transactional(readOnly = true)
    public boolean contains(String url) {
        String query = "SELECT COUNT(*) FROM links WHERE url = ?";
        int count = jdbcClient.sql(query)
            .param(url)
            .query(Integer.class).single();
        return count > 0;
    }

    //Это ок, делать такой метод?
    @Transactional
    public void updateLastUpdate(LinkDTO link, OffsetDateTime updatedTimeToUpdate) {
        String query = "UPDATE links SET last_updated_at=? WHERE url=?;";
        jdbcClient.sql(query).param(updatedTimeToUpdate).param(link.getUrl()).update();
    }

    //Это ок, делать такой метод?
    @Transactional
    public void updateCheckedAt(LinkDTO link, OffsetDateTime updatedCheckedAt) {
        String query = "UPDATE links SET checked_at=? WHERE url=?;";
        jdbcClient.sql(query).param(updatedCheckedAt).param(link.getUrl()).update();
    }
}

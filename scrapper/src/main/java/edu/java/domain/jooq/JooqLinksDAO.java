package edu.java.domain.jooq;

import edu.java.domain.jdbc.dto.LinkDTO;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.domain.jooq.Tables.LINKS;

@Repository
public class JooqLinksDAO {
    DSLContext dslContext;
    private static final String URL_COLUMN_NAME = "url";
    private static final String CHECKED_AT_COLUMN_NAME = "checked_at";
    private static final String LAST_UPDATED_AT_COLUMN_NAME = "last_updated_at";

    @Autowired
    public JooqLinksDAO(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional
    public void add(String url) {
        dslContext.insertInto(LINKS, LINKS.URL).values(url).execute();
    }

    @Transactional
    public void remove(String url) {
        dslContext.deleteFrom(LINKS).where(LINKS.URL.eq(url)).execute();
    }

    public List<LinkDTO> findAll() {
        return dslContext.selectFrom(LINKS).fetchInto(LinkDTO.class);
    }

    public List<LinkDTO> findAllFilteredToCheck(Duration forceCheckDelay) {
        List<LinkDTO> links = dslContext.selectFrom(LINKS)
            .where(LINKS.CHECKED_AT.plus(new YearToSecond(
                new YearToMonth(),
                new DayToSecond(0, 0, 0, (int) forceCheckDelay.getSeconds())
            )).le(OffsetDateTime.now()))
            .fetchInto(LinkDTO.class);
        return links;
    }

    public Long getId(String url) {
        return dslContext.selectFrom(LINKS).where(LINKS.URL.eq(url)).fetchOne().getId();
    }

    public boolean contains(String url) {
        return dslContext.selectCount().from(LINKS).where(LINKS.URL.eq(url))
            .fetchOne().value1() > 0;
    }

    @Transactional
    public void updateLastUpdate(LinkDTO link, OffsetDateTime updatedTimeToUpdate) {
        dslContext.update(LINKS).set(LINKS.LAST_UPDATED_AT, updatedTimeToUpdate).where(LINKS.URL.eq(link.getUrl()))
            .execute();
    }

    @Transactional
    public void updateCheckedAt(LinkDTO link, OffsetDateTime updatedCheckedAt) {
        dslContext.update(LINKS).set(LINKS.CHECKED_AT, updatedCheckedAt).where(LINKS.URL.eq(link.getUrl()))
            .execute();
    }
}

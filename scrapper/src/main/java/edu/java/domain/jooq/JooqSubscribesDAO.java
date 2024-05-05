package edu.java.domain.jooq;

import edu.java.domain.dto.LinkDTO;
import edu.java.domain.dto.SubscribeDTO;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.domain.jooq.Tables.LINKS;
import static edu.java.scrapper.domain.jooq.Tables.SUBSCRIBES;

@Repository
public class JooqSubscribesDAO {
    DSLContext dslContext;

    public JooqSubscribesDAO(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional
    public void add(Long chatId, Long urlId) {
        dslContext.insertInto(SUBSCRIBES, SUBSCRIBES.CHATID, SUBSCRIBES.LINKID).values(chatId, urlId).execute();
    }

    @Transactional
    public void remove(Long chatId, Long urlId) {
        dslContext.deleteFrom(SUBSCRIBES).where(SUBSCRIBES.CHATID.eq(chatId)).and(SUBSCRIBES.LINKID.eq(urlId))
            .execute();
    }

    @Transactional(readOnly = true)
    public List<SubscribeDTO> findAllSubscribes() {
        return dslContext.selectFrom(SUBSCRIBES).fetchInto(SubscribeDTO.class);
    }

    @Transactional(readOnly = true)
    public List<LinkDTO> findAllLinksByChatId(Long chatId) {
        return dslContext.selectFrom(LINKS).where(LINKS.ID.in(dslContext.select(SUBSCRIBES.LINKID).from(SUBSCRIBES)
            .where(SUBSCRIBES.CHATID.eq(chatId)))).fetchInto(LinkDTO.class);
    }

    @Transactional(readOnly = true)
    public boolean contains(Long chatId, Long urlId) {
        return dslContext.selectCount().from(SUBSCRIBES).where(SUBSCRIBES.CHATID.eq(chatId))
            .and(SUBSCRIBES.LINKID.eq(urlId)).fetchOne().value1() > 0;
    }
}

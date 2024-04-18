package edu.java.domain.jooq;

import edu.java.domain.jdbc.dto.ChatDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.domain.jooq.Tables.CHATS;

@Repository
@Slf4j
public class JooqChatsDAO {
    DSLContext dslContext;

    public JooqChatsDAO(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional
    public void add(Long telegramId) {
        dslContext.insertInto(CHATS, CHATS.TELEGRAMID).values(telegramId).execute();
    }

    @Transactional
    public void remove(Long telegramId) {
        dslContext.deleteFrom(CHATS).where(CHATS.TELEGRAMID.eq(telegramId)).execute();
    }

    public List<ChatDTO> findAll() {
        return dslContext.select(CHATS.fields()).from(CHATS).fetchInto(ChatDTO.class);
    }

    public boolean contains(Long chatId) {
        return dslContext.selectCount().from(CHATS).where(CHATS.TELEGRAMID.eq(chatId)).fetchOne().value1() > 0;
    }
}

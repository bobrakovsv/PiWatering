package ru.bserg.watering;

import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bserg.watering.db.tables.Message;

@Service
@Log4j2
public class MsgSvc {

    private final DSLContext dsl;

    private static final Message MSG = Message.MESSAGE;


    @Autowired
    public MsgSvc(DSLContext dsl) {
        this.dsl = dsl;
    }


    /**
     * Создание сообщения в БД и в журнале
     */
    public void put(Integer severity, String text) {
        if (severity == 1)
            log.error(text);
        else if (severity == 2)
            log.warn(text);
        else if (severity == 3)
            log.info(text);
        else
            log.debug(text);

        dsl.insertInto(MSG, MSG.MSG_DATE, MSG.SEVERITY, MSG.TEXT)
                .values(Utils.getCurrentDate(), severity, text)
                .execute();
    }


    /**
     * Создание сообщения в БД и в журнале для исключения
     */
    public void put(String text, Exception ex) {
        log.error(text, ex);
        dsl.insertInto(MSG, MSG.MSG_DATE, MSG.SEVERITY, MSG.TEXT)
                .values(Utils.getCurrentDate(), 1, text)
                .execute();
    }

}

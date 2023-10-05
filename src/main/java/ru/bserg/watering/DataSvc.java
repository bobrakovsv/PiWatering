package ru.bserg.watering;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/**
 * Различные вспомогательные процедуры по работе с данными
 */
@SuppressWarnings("unused")
@Service
public class DataSvc {

    private final DSLContext dsl;


    @Autowired
    public DataSvc(DSLContext dsl) {
        this.dsl = dsl;
    }


    /**
     * Пересоздание алиаса
     */
    public void recreateAlias(String alias, String object) {
        dsl.execute("DROP ALIAS IF EXISTS " + alias);
        dsl.execute("CREATE ALIAS " + alias + " FOR \"" + object + "\"");
    }


    /**
     * Инициализация "хранимых" функций
     */
    public void initStoredFunctions() {
        String p = "ru.bserg.watering.DataSvc.";
        recreateAlias("ЧАС", p + "currentHour");
        recreateAlias("МИН", p + "currentMinute");
        recreateAlias("ВРЕМЯ", p + "currentTime");
        recreateAlias("ДЕНЬ_НЕДЕЛИ", p + "currentDayOfWeek");
    }


    /**
     * Возвращает текущий час
     */
    public static int currentHour() {
        return LocalDateTime.now().getHour();
    }


    /**
     * Возвращает текущую минуту
     */
    public static int currentMinute() {
        return LocalDateTime.now().getMinute();
    }


    /**
     * Возвращает текущее время в формате "ЧЧ:ММ"
     */
    public static String currentTime() {
        LocalDateTime t = LocalDateTime.now();
        return Utils.lpad(Integer.toString(t.getHour()), 2, '0')
                + ":"
                + Utils.lpad(Integer.toString(t.getMinute()), 2, '0');
    }


    /**
     * Возвращает текущее время в формате "ЧЧ:ММ"
     */
    public static int currentDayOfWeek() {
        return LocalDateTime.now().getDayOfWeek().getValue();
    }


    /**
     * Проверка указанного выражения
     */
    public boolean evaluateCondition(String condition) throws Exception {
        Record rec;
        try {
            rec = dsl.selectOne().where(condition).fetchOne();
            return rec != null;
        } catch (Exception ex) {
            throw new Exception("Сбой проверки условия '" + condition + "': " + ex.getMessage());
        }
    }


    /**
     * Вычисление челочисленного выражения
     */
    public int evaluateIntegerExpr(String expression) throws Exception {
        Result<Record> recs;
        Integer res = null;
        try {
            recs = dsl.fetch("select cast(" + expression + " as int)");
            res = (Integer) recs.getValue(0, 0);
        } catch (Exception ex) {
            Utils.err("Сбой вычисления выражения '" + expression + "': " + ex.getMessage());
        }
        if (res == null)
            Utils.err("Сбой вычисления целочисленного выражения '" + expression + "'");
        return res;
    }


    /**
     * Вычисление челочисленного выражения длительности открытия
     */
    public int evaluateDurationlExpr(String expression) throws Exception {
        int res = evaluateIntegerExpr(expression);
        if (res <= 0)
            Utils.err("Длительность открытия должна быть больше ноля");
        return res;
    }

}

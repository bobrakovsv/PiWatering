package ru.bserg.watering;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.UpdatableRecord;
import org.jooq.impl.UpdatableRecordImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;


/**
 * Всякие мелкие полезные статические функции, используемые по всему проекту
 */
@Component
public class Utils {


    /**
     * Функция, аналогичная NVL в oracle
     */
    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }


    /**
     * Возвращает текст сообщения для исключения
     */
    public static String getExceptionText(final Exception e) {
        if (e == null)
            return "Пустое исключение";

        StringBuilder em = new StringBuilder();
        if (e.getMessage() != null)
            em.append(e.getMessage());
        if (e.getCause() != null)
            em.append(" ").append(e.getCause());
        if (em.length() == 0 && e.getClass().getCanonicalName() != null)
            em.append(e.getClass().getCanonicalName());
        if (em.length() > 0)
            return em.toString();
        return "Неизвестное исключение";
    }


    /**
     * Возвращает стандартный ответ серверной части об успешно выполненной операции и какие-либо данные
     */
    public static String restOk(JSONArray dataArray, JSONObject dataObject) {
        JSONObject jo = new JSONObject();
        jo.put("ok", true);
        jo.put("errorCode", 0);
        if (dataArray != null)
            jo.put("data", dataArray);
        if (dataObject != null)
            jo.put("data", dataObject);
        return jo.toString();
    }


    /**
     * Возвращает стандартный ответ серверной части об успешно выполненной операции
     */
    public static String restOk() {
        return restOk(null, null);
    }


    /**
     * Возвращает стандартный ответ серверной части об успешно выполненной операции и какие-либо данные
     */
    public static String restOk(JSONArray data) {
        return restOk(data, null);
    }


    /**
     * Возвращает стандартный ответ серверной части об успешно выполненной операции и какие-либо данные
     */
    public static String restOk(JSONObject data) {
        return restOk(null, data);
    }


    /**
     * Возвращает стандартный ответ серверной части о неуспешно выполненной операции
     */
    public static String restError(Integer code, String error) {
        JSONObject jo = new JSONObject();
        jo.put("ok", false);
        jo.put("errorCode", code);
        jo.put("error", error);
        return jo.toString();
    }


    /**
     * Возвращает стандартный ответ серверной части о неуспешно выполненной операции
     */
    public static String restError(Exception ex) {
        return restError(1, getExceptionText(ex));
    }


    /**
     * Текущая дата
     */
    public static LocalDateTime getCurrentDate() {
        return LocalDateTime.now();
    }


    /**
     * Текущая дата + указанное кол-во миллисекунд
     */
    public static LocalDateTime getNextDate(Long plusMS) {
        return LocalDateTime.now().plus(plusMS, ChronoUnit.MILLIS);
    }


    /**
     * Возвращает строку дефолтного формата даты по дате
     */
    public static String dateToStr(LocalDateTime locDate) {
        if (locDate == null) {
            return null;
        }
        return locDate.format(Cst.dateFormatter);
    }


    /**
     * Возвращает строку дефолтного формата даты по дате
     */
    public static String dateTimeToStr(LocalDateTime locDate) {
        if (locDate == null) {
            return null;
        }
        return locDate.format(Cst.dateTimeFormatter);
    }


    /**
     * Возвращает строку с датой и временем, если дата отличается от текущей,
     * и только временем, если не отличается
     */
    public static String dateTimeToShortStr(LocalDateTime locDate) {
        if (locDate == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (
                locDate.getDayOfYear() == now.getDayOfYear()
                        && locDate.getYear() == now.getYear()
        )
            return locDate.format(Cst.timeFormatter);
        else
            return locDate.format(Cst.dateTimeFormatter);
    }


    /**
     * Преобразование интервала дат в строку с форматом N г. N м. N д. ЧЧ:ММ:СС
     * Часть до ЧЧ:ММ:СС опциональна
     */
    public static String intervalToStr(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null)
            return null;

        StringBuilder res = new StringBuilder();
        LocalDateTime d = LocalDateTime.from(start);

        long years = d.until(end, ChronoUnit.YEARS);
        d = d.plusYears(years);
        if (years > 0)
            res.append(years).append(" ").append("г. ");

        long months = d.until(end, ChronoUnit.MONTHS);
        d = d.plusMonths(months);
        if (months > 0)
            res.append(months).append(" ").append("м. "); // м

        long days = d.until(end, ChronoUnit.DAYS);
        d = d.plusDays(days);
        if (days > 0)
            res.append(days).append(" ").append("д. ");  // д

        long hours = d.until(end, ChronoUnit.HOURS);
        d = d.plusHours(hours);
        res.append(lpad(Long.toString(hours), 2, '0')).append(":");

        long minutes = d.until(end, ChronoUnit.MINUTES);
        d = d.plusMinutes(minutes);
        res.append(lpad(Long.toString(minutes), 2, '0')).append(":");

        long seconds = d.until(end, ChronoUnit.SECONDS);
        res.append(lpad(Long.toString(seconds), 2, '0'));

        return res.toString();
    }


    /**
     * Заснуть на указанное время (или до прерывания)
     * Возвращает false, если было прерывание
     */
    public static boolean sleep(long delay) {
        try {
            Thread.sleep(delay);
            return true;
        } catch (InterruptedException ex) {
            return false;
        }
    }


    /**
     * Преобразует JOOQ Record в map с преобразованием дат в нужный формат
     */
    public static Map<String, Object> recIntoMap(Record record) {
        Map<String, Object> m = record.intoMap();

        for (String key : m.keySet()) {
            Object value = m.get(key);
            if (value instanceof LocalDateTime)
                m.replace(key, dateTimeToStr((LocalDateTime) value));
        }

        return m;
    }


    /**
     * Для всех строковых полей записи удаляются конечные пробелы
     * Если строка пустая (""), то полю присваивается null
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends UpdatableRecord<T>> void cleanStrings(UpdatableRecordImpl<T> r) {
        for (Field f : r.fields()) {
            if (String.class.equals(f.getType())) {
                String val = (String) r.get(f);
                if (val != null) {
                    // Удаление пробелов в конце строки
                    val = val.replaceAll("\\s*$", "");
                    if (val != null && val.isEmpty()) {
                        val = null;
                    }
                    r.set(f, val);
                }
            }
        }
    }


    /**
     * Формат конвертации из строки в BigDecimal
     */
    private static final DecimalFormat decimalFormat = new DecimalFormat();

    static {
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        decimalFormat.setParseBigDecimal(true);
    }


    /**
     * Исключение с указанным текстом
     */
    public static void err(String text) throws Exception {
        throw new Exception(text);
    }


    /**
     * Исключение с указанным текстом для отображения в web-интерфейсе
     * Добавляется html-разметка для красоты
     * text выводится нормальным шрифтом, а details - более мелким
     */
    public static void err4ui(String text, String details) throws Exception {
        if (details == null)
            throw new Exception(text);
        else
            throw new Exception(text + "<br><span class='noty-details'>"
                    + (details.length() > 500 ? details.substring(0, 500) + "..." : details)
                    + "<span>");
    }


    /**
     * Функция для lpad, rpad
     */
    @SuppressWarnings("SameParameterValue")
    private static String pad(String value, int lenght, char symbol, boolean left) {
        if (value == null)
            return null;
        int c = lenght - value.length();
        if (c <= 0)
            return value;
        String s = String.format("%" + c + "s", "").replace(' ', symbol);
        if (left)
            return s + value;
        else
            return value + s;
    }


    /**
     * Функция, аналогичная LPAD в oracle
     */
    public static String lpad(String value, int lenght, char symbol) {
        return pad(value, lenght, symbol, true);
    }

}
